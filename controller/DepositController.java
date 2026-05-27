package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.DepositView;

/**
 * 택배 보관 요청을 처리하는 Controller.
 * 사용자가 DepositView에서 보관을 요청하면,
 * 빈 칸을 탐색하고 인증코드를 발급하여 Package를 배정한다.
 */
public class DepositController {

    private final LockerRepository lockerRepository;
    private final DepositView depositView;

    public DepositController(LockerRepository lockerRepository, DepositView depositView) {
        this.lockerRepository = lockerRepository;
        this.depositView = depositView;
    }

    /**
     * 보관 요청의 진입점.
     * 빈 칸이 없으면 View에 "보관 불가" 메시지를 전달하고 종료한다.
     *
     * @param recipient 수령인 이름
     * @param size      요청 칸 크기 ("small" / "medium" / "large")
     */
    public void handleDeposit(String recipient, String size) {
        // TODO: 구현 예정
    }

    /**
     * 입력된 크기에 맞는 빈 칸을 탐색한다.
     * 동시 접근을 막기 위해 반드시 synchronized 블록 안에서 호출해야 한다.
     *
     * @param size 칸 크기 ("small" / "medium" / "large")
     * @return 빈 칸이 있으면 해당 Locker, 없으면 null
     */
    private Locker findAvailableLocker(String size) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 6자리 난수 인증코드를 생성한다.
     * 예: "472819"
     *
     * @return 6자리 숫자 문자열
     */
    private String generateAuthCode() {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 탐색된 칸에 Package를 배정하고 칸 상태를 "사용 중"으로 변경한다.
     *
     * @param locker 배정할 칸
     * @param pkg    배정할 Package 객체
     */
    private void assignPackage(Locker locker, Package pkg) {
        // TODO: 구현 예정
    }
}