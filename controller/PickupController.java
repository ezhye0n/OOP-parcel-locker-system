package controller;

import model.LockerRepository;
import view.PickupView;

/**
 * 택배 수령 요청을 처리하는 Controller.
 * 사용자가 입력한 인증코드를 Repository에 전달하고,
 * 처리 결과를 View에 전달한다.
 *
 * Tell, Don't Ask 적용:
 * lockerRepository.pickup()이 탐색·검증·상태 변경·파일 저장을 모두 처리한다.
 * Controller는 결과 문자열만 받아 View에 전달하는 역할만 담당한다.
 * 성공 여부를 문자열로 판단하는 취약한 구조가 사라졌다.
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
     * 형식 검증 → Repository에 수령 위임 → View에 결과 전달 순으로 처리한다.
     *
     * @param authCode 사용자가 입력한 6자리 인증코드
     */
    public void handlePickup(String authCode) {
        // 형식 검증: 6자리 숫자인지 확인 (도메인 규칙이 아닌 입력값 방어)
        if (!isValidCodeFormat(authCode)) {
            pickupView.showResult("인증코드는 6자리 숫자입니다.");
            return;
        }

        // 탐색·검증·상태 변경·파일 저장을 Repository에 위임 (Tell, Don't Ask)
        // pickup()이 저장까지 처리하므로, Controller가 성공 여부를 별도로 판단하지 않아도 된다.
        String result = lockerRepository.pickup(authCode);
        pickupView.showResult(result);
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
}
