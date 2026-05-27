package view;

import javax.swing.*;

/**
 * 택배 수령 화면.
 * 6자리 인증코드를 입력받아 수령을 처리하고 결과를 표시한다.
 */
public class PickupView extends JFrame {

    private JTextField authCodeField; // 인증코드 입력창
    private JButton pickupButton;     // 수령 버튼
    private JLabel resultLabel;       // 결과 메시지 출력 (성공/실패)

    public PickupView() {
        // TODO: UI 초기화
    }

    /**
     * 사용자가 입력한 인증코드를 반환한다.
     *
     * @return 6자리 인증코드 문자열
     */
    public String getAuthCode() {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 수령 결과 메시지를 화면에 표시한다.
     * 성공 시 "수령 완료", 실패 시 오류 원인을 표시한다.
     *
     * @param message 표시할 메시지
     */
    public void showResult(String message) {
        // TODO: 구현 예정
    }

    /**
     * 수령 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addPickupListener(java.awt.event.ActionListener listener) {
        pickupButton.addActionListener(listener);
    }
}
