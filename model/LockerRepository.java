package model;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 전체 택배함 데이터를 관리하고 파일 I/O를 담당하는 Repository.
 *
 * 설계 의도:
 * - Controller는 저장 방식·내부 컬렉션을 모른 채 메서드만 호출한다(결합도 ↓).
 * - 만료 감지 스레드와 사용자 처리가 같은 데이터를 공유하므로 public 메서드를
 *   synchronized로 선언해 동시 접근을 막는다(11주차).
 * - 파일 I/O는 객체 직렬화로 구현하고, 파일이 없거나 손상되면 기본 데이터로 복구한다.
 *
 * TOCTOU 해결:
 * - assignFirstAvailable()이 "빈 칸 탐색 + 배정"을 하나의 synchronized 블록으로 처리한다.
 *
 * Tell, Don't Ask 적용:
 * - pickup()이 "인증코드 탐색 + 상태 검증 + 수령 처리 + 파일 저장"을
 *   하나의 synchronized 블록으로 처리한다.
 *   Controller가 결과 문자열만 받아 View에 전달하면 된다.
 */
public class LockerRepository implements Serializable {

    private static final long serialVersionUID = 3L;

    private static final String FILE_PATH = "lockers.dat";
    private static final int DEFAULT_LOCKER_COUNT_PER_SIZE = 5;

    /** 전체 칸 데이터. key는 lockerId, value는 Locker 객체이다. */
    private final Map<String, Locker> lockers = new HashMap<>();

    /** 저장 파일에서 데이터를 불러온다. 파일이 없거나 손상되면 기본 데이터로 복구한다. */
    public synchronized void load() {
        File file = new File(FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            resetToDefaultLockers();
            save();
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object loadedObject = inputStream.readObject();
            Map<String, Locker> loadedLockers = castToLockerMap(loadedObject);

            lockers.clear();
            lockers.putAll(loadedLockers);

            if (lockers.isEmpty()) {
                resetToDefaultLockers();
                save();
            }
        } catch (EOFException e) {
            resetToDefaultLockers();
            save();
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            System.err.println("택배함 데이터를 불러오지 못해 기본 데이터로 초기화합니다: " + e.getMessage());
            resetToDefaultLockers();
            save();
        }
    }

    /** 현재 택배함 데이터를 파일에 저장한다. */
    public synchronized void save() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            outputStream.writeObject(lockers);
        } catch (IOException e) {
            System.err.println("택배함 데이터를 저장하지 못했습니다: " + e.getMessage());
        }
    }

    /**
     * 지정된 크기의 빈 칸을 탐색하여 Package를 원자적으로 배정한다.
     *
     * TOCTOU 경쟁 조건 방지:
     * getAvailableLockers()와 assign()을 Controller에서 따로 호출하면
     * 두 호출 사이에 다른 스레드가 같은 칸을 선점할 수 있다.
     * 이 메서드는 탐색과 배정을 하나의 synchronized 블록으로 묶어 원자성을 보장한다.
     *
     * @param size 요청 칸 크기
     * @param pkg  배정할 Package
     * @return 배정에 성공한 Locker. 빈 칸이 없으면 Optional.empty()
     */
    public synchronized Optional<Locker> assignFirstAvailable(LockerSize size, Package pkg) {
        if (size == null) {
            throw new IllegalArgumentException("size는 null일 수 없습니다.");
        }
        if (pkg == null) {
            throw new IllegalArgumentException("pkg는 null일 수 없습니다.");
        }

        List<Locker> candidates = new ArrayList<>();
        for (Locker locker : lockers.values()) {
            if (locker.getLockerSize() == size && locker.isAvailable()) {
                candidates.add(locker);
            }
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        Collections.sort(candidates);
        Locker target = candidates.get(0);
        target.assign(pkg);
        return Optional.of(target);
    }

    /**
     * 인증코드로 칸을 찾아 수령 처리를 원자적으로 수행하고 파일에 저장한다.
     *
     * Tell, Don't Ask 적용:
     * 탐색·검증·상태 변경·저장을 이 메서드 안에서 모두 처리한다.
     * Controller는 결과 문자열만 받아 View에 전달하면 되므로,
     * 성공 여부를 문자열로 판단하는 취약한 구조가 사라진다.
     *
     * @param authCode 사용자가 입력한 인증코드
     * @return 처리 결과 문자열 (View에 그대로 표시 가능)
     */
    public synchronized String pickup(String authCode) {
        // 인증코드로 사용 중인 칸 탐색
        Locker targetLocker = null;
        for (Locker locker : lockers.values()) {
            if (!locker.isOccupied()) continue;
            Package pkg = locker.getAssignedPackage();
            if (pkg != null && pkg.verifyAuthCode(authCode)) {
                targetLocker = locker;
                break;
            }
        }

        if (targetLocker == null) {
            return "인증코드가 올바르지 않습니다.";
        }

        Package pkg = targetLocker.getAssignedPackage();

        // 탐색 직후 다른 스레드가 수령을 완료한 극단적 경쟁 상황
        if (pkg == null) {
            return "처리 중 오류가 발생했습니다. 다시 시도해주세요.";
        }

        if (pkg.isPickedUp()) {
            return "이미 수령된 택배입니다.";
        }

        if (pkg.isExpired()) {
            return "보관 기간이 만료된 택배입니다. 관리자에게 문의하세요.";
        }

        // 수령 처리: Package 상태 변경 → 칸 해제 → 파일 저장
        // 저장까지 이 메서드가 책임지므로, Controller가 성공 여부를 판단할 필요가 없다.
        pkg.markAsPickedUp();
        targetLocker.release();
        save();

        return "수령 완료!\n수령인: " + pkg.getRecipient() + "\n칸 번호: " + targetLocker.getLockerId();
    }

    /** 특정 크기의 빈 택배함 목록을 반환한다(한글 크기 문자열). */
    public synchronized List<Locker> getAvailableLockers(String size) {
        return getAvailableLockers(LockerSize.fromLabel(size));
    }

    /** 특정 크기의 빈 택배함 목록을 반환한다. */
    public synchronized List<Locker> getAvailableLockers(LockerSize size) {
        if (size == null) {
            throw new IllegalArgumentException("size는 null일 수 없습니다.");
        }
        List<Locker> result = new ArrayList<>();
        for (Locker locker : lockers.values()) {
            if (locker.getLockerSize() == size && locker.isAvailable()) {
                result.add(locker);
            }
        }
        Collections.sort(result);
        return result;
    }

    /** 전체 택배함 목록을 칸 ID 순으로 반환한다. */
    public synchronized List<Locker> getAllLockers() {
        List<Locker> result = new ArrayList<>(lockers.values());
        Collections.sort(result);
        return result;
    }

    /** 칸 ID로 특정 택배함을 조회한다. */
    public synchronized Locker findById(String lockerId) {
        if (lockerId == null || lockerId.trim().isEmpty()) {
            return null;
        }
        return lockers.get(lockerId.trim());
    }

    /** 만료된 택배가 들어 있는 칸 목록을 반환한다. */
    public synchronized List<Locker> getExpiredLockers() {
        List<Locker> result = new ArrayList<>();
        for (Locker locker : lockers.values()) {
            if (locker.hasExpiredPackage()) {
                result.add(locker);
            }
        }
        Collections.sort(result);
        return result;
    }

    /** 보관 기간을 초과한 택배들을 만료 처리하고, 새로 만료된 개수를 반환한다(ExpiryMonitor가 호출). */
    public synchronized int markOverduePackagesAsExpired(int maxStorageDays) {
        int expiredCount = 0;
        for (Locker locker : lockers.values()) {
            Package pkg = locker.getAssignedPackage();
            if (pkg != null && pkg.isStored() && pkg.isOverStorageLimit(maxStorageDays)) {
                pkg.markAsExpired();
                expiredCount++;
            }
        }
        if (expiredCount > 0) {
            save();
        }
        return expiredCount;
    }

    /** 기본 택배함 목록으로 초기화한다(파일이 없거나 손상된 경우). */
    private void resetToDefaultLockers() {
        lockers.clear();
        for (int i = 1; i <= DEFAULT_LOCKER_COUNT_PER_SIZE; i++) {
            for (LockerSize size : LockerSize.values()) {
                addLocker(createLocker(size, i));
            }
        }
    }

    private Locker createLocker(LockerSize size, int index) {
        String lockerId = String.format("%s-%02d", size.getIdPrefix(), index);
        switch (size) {
            case SMALL:
                return new SmallLocker(lockerId);
            case MEDIUM:
                return new MediumLocker(lockerId);
            case LARGE:
                return new LargeLocker(lockerId);
            default:
                throw new IllegalArgumentException("지원하지 않는 칸 크기입니다: " + size);
        }
    }

    private void addLocker(Locker locker) {
        if (locker == null) {
            throw new IllegalArgumentException("locker는 null일 수 없습니다.");
        }
        String lockerId = locker.getLockerId();
        if (lockers.containsKey(lockerId)) {
            throw new IllegalArgumentException("중복된 택배함 ID입니다: " + lockerId);
        }
        lockers.put(lockerId, locker);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Locker> castToLockerMap(Object loadedObject) {
        if (!(loadedObject instanceof Map)) {
            throw new IllegalArgumentException("저장 파일 형식이 올바르지 않습니다.");
        }
        Map<String, Locker> result = new HashMap<>();
        Map<Object, Object> rawMap = (Map<Object, Object>) loadedObject;
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Locker)) {
                throw new IllegalArgumentException("저장된 택배함 데이터가 올바르지 않습니다.");
            }
            result.put((String) entry.getKey(), (Locker) entry.getValue());
        }
        return result;
    }
}
