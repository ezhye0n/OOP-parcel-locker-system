package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.PickupView;

/**
 * 택배 수령 요청을 처리하는 Controller.
 * 사용자가 입력한 인증코드를 검증하고,
 * 유효하면 칸을 해제하여 수령을 완료한다.
 */
public class PickupController {

    private final LockerRepository lockerRepository;
    private final PickupView pickupView;

    public PickupController(LockerRepository lockerRepository, PickupView pickupView) {
        this.lockerRepository = lockerRepository;
        this.pickupView = pickupView;
    }

    /**
     * 수령 요청의 진입점.
     * 코드 불일치 또는 만료 시 View에 오류 메시지를 전달하고 종료한다.
     *
     * @param authCode 사용자가 입력한 6자리 인증코드
     */
    public void handlePickup(String authCode) {
        // TODO: 구현 예정
    }

    /**
     * 입력된 코드와 저장된 코드를 비교한다.
     *
     * @param authCode 사용자가 입력한 인증코드
     * @return 일치하면 true, 불일치하면 false
     */
    private boolean validateCode(String authCode) {
        // TODO: 구현 예정
        return false;
    }

    /**
     * 보관 시각을 기준으로 3일 초과 여부를 확인한다.
     *
     * @param pkg 확인할 Package 객체
     * @return 만료되었으면 true, 유효하면 false
     */
    private boolean isExpired(Package pkg) {
        // TODO: 구현 예정
        return false;
    }

    /**
     * 수령 완료 후 칸 상태를 "비어있음"으로 전환한다.
     *
     * @param locker 해제할 칸
     */
    private void releaseLocker(Locker locker) {
        // TODO: 구현 예정
    }
}