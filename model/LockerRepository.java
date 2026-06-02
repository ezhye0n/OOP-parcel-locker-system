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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 전체 택배함 데이터를 관리하고 파일 I/O를 담당하는 Repository 클래스.
 *
 * <p>Controller는 파일 저장 방식이나 내부 컬렉션 구조를 알 필요 없이 이 클래스의
 * 메서드만 호출한다. 이를 통해 Model과 Controller의 결합도를 낮춘다.</p>
 *
 * <p>동기화 전략: DepositController, PickupController, AdminController,
 * ExpiryMonitor가 같은 Repository 인스턴스를 공유할 수 있으므로 public 메서드를
 * synchronized로 선언했다. 동시에 여러 요청이 들어와도 한 번에 하나의 작업만
 * lockers 상태에 접근한다.</p>
 */
public class LockerRepository implements Serializable {

    private static final long serialVersionUID = 2L;

    private static final String FILE_PATH = "lockers.dat";
    private static final int DEFAULT_LOCKER_COUNT_PER_SIZE = 5;

    /** 전체 칸 데이터. key는 lockerId, value는 Locker 객체이다. */
    private final Map<String, Locker> lockers = new LinkedHashMap<>();

    /**
     * 저장 파일에서 택배함 데이터를 불러온다.
     * 파일이 없거나 읽기 실패가 발생하면 기본 택배함 목록으로 복구한다.
     */
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

    /**
     * 현재 택배함 데이터를 파일에 저장한다.
     * 보관, 수령, 만료, 관리자 강제 해제 후 호출된다.
     */
    public synchronized void save() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            outputStream.writeObject(lockers);
        } catch (IOException e) {
            System.err.println("택배함 데이터를 저장하지 못했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 크기의 빈 택배함 목록을 반환한다.
     * 반환 리스트는 새 리스트이므로 외부에서 수정해도 내부 컬렉션은 바뀌지 않는다.
     *
     * @param size 칸 크기 (소형, 중형, 대형)
     * @return 해당 크기의 빈 Locker 리스트
     */
    public synchronized List<Locker> getAvailableLockers(String size) {
        return getAvailableLockers(LockerSize.fromLabel(size));
    }

    /**
     * 특정 크기의 빈 택배함 목록을 타입 안전한 방식으로 반환한다.
     *
     * @param size LockerSize enum
     * @return 해당 크기의 빈 Locker 리스트
     */
    public synchronized List<Locker> getAvailableLockers(LockerSize size) {
        Objects.requireNonNull(size, "size는 null일 수 없습니다.");
        List<Locker> result = new ArrayList<>();

        for (Locker locker : lockers.values()) {
            if (locker.getLockerSize() == size && locker.isAvailable()) {
                result.add(locker);
            }
        }

        sortByLockerId(result);
        return result;
    }

    /**
     * 전체 택배함 목록을 반환한다.
     * 화면 표시 순서가 일정하도록 칸 ID 기준으로 정렬한다.
     *
     * @return 전체 Locker 리스트
     */
    public synchronized List<Locker> getAllLockers() {
        List<Locker> result = new ArrayList<>(lockers.values());
        sortByLockerId(result);
        return result;
    }

    /**
     * 칸 ID로 특정 택배함을 조회한다.
     *
     * @param lockerId 조회할 칸 ID
     * @return 해당 Locker, 없으면 null
     */
    public synchronized Locker findById(String lockerId) {
        if (lockerId == null || lockerId.trim().isEmpty()) {
            return null;
        }
        return lockers.get(lockerId.trim());
    }

    /**
     * 만료된 택배가 들어 있는 칸 목록을 반환한다.
     *
     * @return 만료 상태의 Locker 리스트
     */
    public synchronized List<Locker> getExpiredLockers() {
        List<Locker> result = new ArrayList<>();

        for (Locker locker : lockers.values()) {
            if (locker.hasExpiredPackage()) {
                result.add(locker);
            }
        }

        sortByLockerId(result);
        return result;
    }

    /**
     * 보관 기간을 초과한 택배가 들어 있는 칸 목록을 반환한다.
     * ExpiryMonitor가 기간 계산 방식을 직접 알지 않아도 되도록 Model에 규칙을 둔다.
     *
     * @param maxStorageDays 최대 보관 일수
     * @return 보관 기간을 초과한 Locker 리스트
     */
    public synchronized List<Locker> getOverdueLockers(int maxStorageDays) {
        List<Locker> result = new ArrayList<>();

        for (Locker locker : lockers.values()) {
            if (locker.hasOverduePackage(maxStorageDays)) {
                result.add(locker);
            }
        }

        sortByLockerId(result);
        return result;
    }

    /**
     * 보관 기간을 초과한 택배들을 만료 상태로 전환한다.
     *
     * @param maxStorageDays 최대 보관 일수
     * @return 새로 만료 처리된 택배 수
     */
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

    /**
     * 기본 택배함 목록으로 초기화한다.
     * 테스트나 파일 복구 상황에서 사용한다.
     */
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
        Objects.requireNonNull(locker, "locker는 null일 수 없습니다.");
        String lockerId = locker.getLockerId();
        if (lockers.containsKey(lockerId)) {
            throw new IllegalArgumentException("중복된 택배함 ID입니다: " + lockerId);
        }
        lockers.put(lockerId, locker);
    }

    private void sortByLockerId(List<Locker> lockerList) {
        lockerList.sort(Comparator.comparing(Locker::getLockerId));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Locker> castToLockerMap(Object loadedObject) {
        if (!(loadedObject instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("저장 파일 형식이 올바르지 않습니다.");
        }

        Map<?, ?> rawMap = (Map<?, ?>) loadedObject;
        Map<String, Locker> loadedLockers = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Locker)) {
                throw new IllegalArgumentException("저장된 택배함 데이터가 올바르지 않습니다.");
            }
            loadedLockers.put((String) entry.getKey(), (Locker) entry.getValue());
        }

        return Collections.unmodifiableMap(loadedLockers);
    }
}
