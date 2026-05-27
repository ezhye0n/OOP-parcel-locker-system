package view;

import javax.swing.*;

/**
 * 택배 보관 요청 화면.
 * 수령인 이름 입력, 칸 크기 선택, 보관 버튼을 제공한다.
 * 보관 완료 시 발급된 인증코드와 칸 번호를 표시한다.
 */
public class DepositView extends JFrame {

    private JTextField recipientField;   // 수령인 이름 입력창
    private JComboBox<String> sizeCombo; // 칸 크기 선택 (소형/중형/대형)
    private JButton depositButton;       // 보관 버튼
    private JLabel resultLabel;          // 결과 메시지 출력 (인증코드, 오류 등)

    public DepositView() {
        // TODO: UI 초기화
    }

    /**
     * 사용자가 입력한 수령인 이름을 반환한다.
     *
     * @return 수령인 이름 문자열
     */
    public String getRecipient() {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 사용자가 선택한 칸 크기를 반환한다.
     *
     * @return 칸 크기 ("소형" / "중형" / "대형")
     */
    public String getSelectedSize() {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 보관 결과 메시지를 화면에 표시한다.
     * 성공 시 인증코드와 칸 번호, 실패 시 오류 메시지를 표시한다.
     *
     * @param message 표시할 메시지
     */
    public void showResult(String message) {
        // TODO: 구현 예정
    }

    /**
     * 보관 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addDepositListener(java.awt.event.ActionListener listener) {
        depositButton.addActionListener(listener);
    }
}
