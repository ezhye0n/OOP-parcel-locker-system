package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.DepositView;

import java.util.List;
import java.util.Random;

/**
 * 택배 보관 요청을 처리하는 Controller.
 * 사용자가 DepositView에서 보관을 요청하면,
 * 빈 칸을 탐색하고 인증코드를 발급하여 Package를 배정한다.
 *
 * 동기화 전략:
 * LockerRepository의 public 메서드가 이미 synchronized로 선언되어 있으므로,
 * Controller에서 별도의 synchronized 블록을 추가하지 않는다.
 */
public class DepositController {

    private final LockerRepository lockerRepository;
    private final DepositView depositView;

    private static final Random RANDOM = new Random();

    public DepositController(LockerRepository lockerRepository, DepositView depositView) {
        this.lockerRepository = lockerRepository;
        this.depositView = depositView;
    }

    /**
     * 보관 요청의 진입점.
     * 입력값 검증 → 빈 칸 탐색 → Package 생성 → 칸 배정 → 파일 저장 순으로 처리한다.
     * 빈 칸이 없거나 입력값이 유효하지 않으면 View에 오류 메시지를 전달하고 종료한다.
     *
     * @param recipient 수령인 이름
     * @param size      요청 칸 크기 ("소형" / "중형" / "대형")
     */
    public void handleDeposit(String recipient, String size) {
        // 입력값 검증: 수령인 이름이 비어있으면 처리 중단
        if (recipient == null || recipient.trim().isEmpty()) {
            depositView.showResult("수령인 이름을 입력해주세요.");
            return;
        }

        // 해당 크기의 빈 칸 탐색
        Locker availableLocker = findAvailableLocker(size);

        // 빈 칸이 없으면 처리 중단
        if (availableLocker == null) {
            depositView.showResult("선택한 크기의 빈 칸이 없습니다.");
            return;
        }

        // 인증코드 발급 및 Package 생성
        String authCode = generateAuthCode();
        String trackingNumber = generateTrackingNumber();
        Package pkg = new Package(trackingNumber, recipient.trim(), authCode);

        // 칸에 Package 배정
        // assign()은 이미 사용 중인 칸에 배정 시 IllegalStateException을 던지도록 Model에서 보호한다.
        // getAvailableLockers()가 synchronized이므로 동시 접근에서도 빈 칸이 보장된다.
        availableLocker.assign(pkg);

        // 변경된 데이터 파일에 저장
        lockerRepository.save();

        // 결과를 View에 전달
        // 줄바꿈 포맷은 View(showResult)가 처리하도록 개행 문자로 전달한다.
        depositView.showResult(
            "보관 완료!\n" +
            "칸 번호: " + availableLocker.getLockerId() + "\n" +
            "인증코드: " + authCode + "\n" +
            "수령 시 인증코드를 반드시 기억해주세요."
        );
    }

    /**
     * 입력된 크기에 맞는 빈 칸을 탐색한다.
     * LockerRepository.getAvailableLockers()가 synchronized이므로
     * 동시 접근이 발생해도 안전하다.
     *
     * @param size 칸 크기 ("소형" / "중형" / "대형")
     * @return 빈 칸이 있으면 해당 Locker, 없으면 null
     */
    private Locker findAvailableLocker(String size) {
        List<Locker> availableLockers = lockerRepository.getAvailableLockers(size);
        if (availableLockers.isEmpty()) {
            return null;
        }
        // 첫 번째 빈 칸 반환
        return availableLockers.get(0);
    }

    /**
     * 6자리 난수 인증코드를 생성한다.
     * 100000 ~ 999999 범위의 숫자를 문자열로 반환한다.
     *
     * @return 6자리 숫자 문자열 (예: "472819")
     */
    private String generateAuthCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 송장번호를 생성한다.
     * 현재 시각 기반으로 고유한 번호를 생성한다.
     *
     * @return 송장번호 문자열 (예: "TRK1717383600000")
     */
    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis();
    }
}
