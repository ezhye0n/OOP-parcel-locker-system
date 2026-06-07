package controller;

import model.Locker;
import model.LockerRepository;
import model.LockerSize;
import model.Package;
import view.DepositView;

import java.util.Optional;
import java.util.Random;

/**
 * 택배 보관 요청을 처리하는 Controller.
 * 사용자가 DepositView에서 보관을 요청하면,
 * 빈 칸을 원자적으로 탐색·배정하고 인증코드를 발급한다.
 *
 * TOCTOU 해결:
 * 빈 칸 탐색과 배정을 Repository.assignFirstAvailable()에 위임하여
 * 단일 synchronized 블록에서 원자적으로 처리한다.
 *
 * 타입 안전성:
 * size 파라미터를 String 대신 LockerSize enum으로 받는다.
 * "소형" / "중형" / "대형" 문자열 변환은 View(DepositView.getSelectedSize())가 담당한다.
 * Controller는 타입이 보장된 값만 다루므로, 오타·잘못된 문자열로 인한 버그가 사라진다.
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
     * 입력값 검증 → Package 생성 → 원자적 탐색·배정 → 파일 저장 → 결과 전달 순으로 처리한다.
     *
     * @param recipient 수령인 이름
     * @param size      요청 칸 크기 (LockerSize enum — 문자열 변환은 View가 담당)
     */
    public void handleDeposit(String recipient, LockerSize size) {
        // 입력값 검증: 수령인 이름이 비어있으면 처리 중단
        if (recipient == null || recipient.trim().isEmpty()) {
            depositView.showError("수령인 이름을 입력해주세요.");
            return;
        }

        // 인증코드·Package 생성
        String authCode = generateAuthCode();
        String trackingNumber = generateTrackingNumber();
        Package pkg = new Package(trackingNumber, recipient.trim(), authCode);

        // 빈 칸 탐색과 배정을 하나의 원자적 연산으로 처리 (TOCTOU 방지)
        Optional<Locker> assigned = lockerRepository.assignFirstAvailable(size, pkg);

        if (!assigned.isPresent()) {
            depositView.showError("선택한 크기의 빈 칸이 없습니다.");
            return;
        }

        // 배정 성공: 변경된 데이터 파일에 저장
        lockerRepository.save();

        // 칸 번호와 인증코드를 View에 전달 — 표시 포맷은 View가 결정한다
        depositView.showSuccess(assigned.get().getLockerId(), authCode);
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
     * 현재 시각(밀리초) 기반으로 고유한 송장번호를 생성한다.
     * 호출 시각에 따라 값이 달라지므로 Javadoc에 예시를 고정하지 않는다.
     *
     * @return "TRK" + 현재 시각 밀리초로 구성된 송장번호 문자열
     */
    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis();
    }
}
