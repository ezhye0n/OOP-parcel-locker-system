package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.PickupView;

import java.util.List;

/**
 * 택배 수령 요청을 처리하는 Controller.
 * 사용자가 입력한 인증코드를 검증하고,
 * 유효하면 칸을 해제하여 수령을 완료한다.
 *
 * 동기화 전략:
 * LockerRepository의 public 메서드가 이미 synchronized로 선언되어 있으므로,
 * Controller에서 별도의 synchronized 블록을 추가하지 않는다.
 *
 * 예외 처리:
 * - 형식 오류:    ERR_INVALID_FORMAT
 * - 코드 불일치: ERR_CODE_MISMATCH
 * - 처리 오류:   ERR_PROCESSING
 * - 이미 수령됨: ERR_ALREADY_PICKED_UP
 * - 만료된 택배: ERR_EXPIRED
 * - 저장 실패:   ERR_SAVE_FAILED
 */
public class PickupController {

    private final LockerRepository lockerRepository;
    private final PickupView pickupView;

    // 오류 메시지를 상수로 선언하여 오타를 방지하고 수정을 한 곳에서 관리한다.
    private static final String ERR_INVALID_FORMAT    = "인증코드는 6자리 숫자입니다.";
    private static final String ERR_CODE_MISMATCH     = "인증코드가 올바르지 않습니다.";
    private static final String ERR_PROCESSING        = "처리 중 오류가 발생했습니다. 다시 시도해주세요.";
    private static final String ERR_ALREADY_PICKED_UP = "이미 수령된 택배입니다.";
    private static final String ERR_EXPIRED           = "보관 기간이 만료된 택배입니다. 관리자에게 문의하세요.";
    private static final String ERR_SAVE_FAILED       = "저장 중 오류가 발생했습니다. 관리자에게 문의하세요.";

    public PickupController(LockerRepository lockerRepository, PickupView pickupView) {
        this.lockerRepository = lockerRepository;
        this.pickupView = pickupView;
    }

    /**
     * 수령 요청의 진입점.
     * 형식 검증 → 코드 일치 확인 → 수령·만료 상태 확인 → 칸 해제 → 파일 저장 순으로 처리한다.
     *
     * @param authCode 사용자가 입력한 6자리 인증코드
     */
    public void handlePickup(String authCode) {
        // 인증코드 형식 검증: 6자리 숫자인지 확인
        if (!isValidCodeFormat(authCode)) {
            pickupView.showResult(ERR_INVALID_FORMAT);
            return;
        }

        // 인증코드에 해당하는 칸 탐색
        Locker targetLocker = findLockerByAuthCode(authCode);

        // 코드 불일치: 해당 칸 없음
        if (targetLocker == null) {
            pickupView.showResult(ERR_CODE_MISMATCH);
            return;
        }

        // 칸에 배정된 Package 조회
        // isOccupied() 체크 후에도 getAssignedPackage()가 null을 반환하는
        // 예외적 상황을 방어하기 위해 null 체크를 추가한다.
        // (예: 매우 드물지만 2명 동시 접근으로 방금 막 다른 누군가가 이미 수령해버린 상황)
        Package pkg = targetLocker.getAssignedPackage();
        if (pkg == null) {
            pickupView.showResult(ERR_PROCESSING);
            return;
        }

        // 이미 수령된 택배인지 확인
        if (pkg.isPickedUp()) {
            pickupView.showResult(ERR_ALREADY_PICKED_UP);
            return;
        }

        // 만료 여부 확인 — Model(Package)이 상태 규칙을 가진다
        if (pkg.isExpired()) {
            pickupView.showResult(ERR_EXPIRED);
            return;
        }

        // 수령 처리: Package 상태 변경 → 칸 해제
        pkg.markAsPickedUp();
        releaseLocker(targetLocker);

        // 변경된 데이터 파일에 저장
        // 저장 실패 시 수령은 완료됐지만 데이터가 유실될 수 있으므로, 사용자에게 즉시 안내한다.
        try {
            lockerRepository.save();
        } catch (Exception e) {
            pickupView.showResult(ERR_SAVE_FAILED);
            return;
        }

        // 수령 성공 결과를 View에 전달 — 표시 포맷은 View가 결정한다
        pickupView.showResult(
            "수령 완료!\n" +
            "수령인: " + pkg.getRecipient() + "\n" +
            "칸 번호: " + targetLocker.getLockerId()
        );
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
            if (!locker.isOccupied()) continue;

            Package pkg = locker.getAssignedPackage();
            if (pkg != null && pkg.verifyAuthCode(authCode)) {
                return locker;
            }
        }
        return null;
    }

    /**
     * 수령 완료 후 칸 상태를 "비어있음"으로 전환한다.
     * 향후 해제 전후 처리(로깅, 알림 등)를 추가하기 위한 확장 포인트로 분리했다.
     *
     * @param locker 해제할 칸
     */
    private void releaseLocker(Locker locker) {
        locker.release();
    }
}
