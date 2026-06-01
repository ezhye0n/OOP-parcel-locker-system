package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.PickupView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 택배 수령 요청을 처리하는 Controller.
 * 사용자가 입력한 인증코드를 검증하고,
 * 유효하면 칸을 해제하여 수령을 완료한다.
 *
 * 예외 처리:
 * - 코드 불일치: "인증코드가 올바르지 않습니다."
 * - 만료된 택배: "보관 기간이 만료된 택배입니다. 관리자에게 문의하세요."
 * - 이미 수령됨: "이미 수령된 택배입니다."
 * - 형식 오류: "인증코드는 6자리 숫자입니다."
 */
public class PickupController {

    private final LockerRepository lockerRepository;
    private final PickupView pickupView;

    /** 보관 기간 제한 (일) */
    private static final int MAX_STORAGE_DAYS = 3;

    public PickupController(LockerRepository lockerRepository, PickupView pickupView) {
        this.lockerRepository = lockerRepository;
        this.pickupView = pickupView;
    }

    /**
     * 수령 요청의 진입점.
     * 형식 검증 → 코드 일치 확인 → 만료 확인 → 칸 해제 → 파일 저장 순으로 처리한다.
     *
     * @param authCode 사용자가 입력한 6자리 인증코드
     */
    public void handlePickup(String authCode) {
        // 인증코드 형식 검증: 6자리 숫자인지 확인
        if (!isValidCodeFormat(authCode)) {
            pickupView.showResult("인증코드는 6자리 숫자입니다.");
            return;
        }

        synchronized (lockerRepository) {
            // 인증코드에 해당하는 칸 탐색
            Locker targetLocker = findLockerByAuthCode(authCode);

            // 코드 불일치: 해당 칸 없음
            if (targetLocker == null) {
                pickupView.showResult("인증코드가 올바르지 않습니다.");
                return;
            }

            Package pkg = targetLocker.getAssignedPackage();

            // 이미 수령된 택배인지 확인
            if (pkg.isPickedUp()) {
                pickupView.showResult("이미 수령된 택배입니다.");
                return;
            }

            // 만료 여부 확인
            if (isExpired(pkg)) {
                pickupView.showResult("보관 기간이 만료된 택배입니다. 관리자에게 문의하세요.");
                return;
            }

            // 수령 처리: 칸 해제 및 Package 상태 변경
            pkg.markAsPickedUp();
            releaseLocker(targetLocker);

            // 변경된 데이터 파일에 저장
            lockerRepository.save();

            pickupView.showResult(
                "수령 완료!\n" +
                "수령인: " + pkg.getRecipient() + "\n" +
                "칸 번호: " + targetLocker.getLockerId()
            );
        }
    }

    /**
     * 인증코드 형식을 검증한다.
     * 6자리 숫자 문자열인지 확인한다.
     *
     * @param authCode 검증할 인증코드
     * @return 유효한 형식이면 true, 아니면 false
     */
    private boolean isValidCodeFormat(String authCode) {
        if (authCode == null) return false;
        return authCode.matches("\\d{6}");
    }

    /**
     * 인증코드로 해당 칸을 탐색한다.
     * 전체 칸을 순회하여 코드가 일치하는 Package가 있는 칸을 반환한다.
     *
     * @param authCode 탐색할 인증코드
     * @return 코드가 일치하는 Locker, 없으면 null
     */
    private Locker findLockerByAuthCode(String authCode) {
        List<Locker> allLockers = lockerRepository.getAllLockers();

        for (Locker locker : allLockers) {
            // 사용 중인 칸만 확인
            if (!locker.isOccupied()) continue;

            Package pkg = locker.getAssignedPackage();
            if (pkg != null && pkg.verifyAuthCode(authCode)) {
                return locker;
            }
        }
        return null;
    }

    /**
     * 보관 시각을 기준으로 3일 초과 여부를 확인한다.
     *
     * @param pkg 확인할 Package 객체
     * @return 만료되었으면 true, 유효하면 false
     */
    private boolean isExpired(Package pkg) {
        long daysSinceStored = ChronoUnit.DAYS.between(pkg.getStoredAt(), LocalDateTime.now());
        return daysSinceStored > MAX_STORAGE_DAYS;
    }

    /**
     * 수령 완료 후 칸 상태를 "비어있음"으로 전환한다.
     *
     * @param locker 해제할 칸
     */
    private void releaseLocker(Locker locker) {
        locker.release();
    }
}
