package view;

import model.LockerSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 택배 보관 요청 화면.
 * 수령인 이름 입력, 칸 크기 선택, 보관 버튼을 제공한다.
 * 보관 완료 시 발급된 인증코드와 칸 번호를 표시한다.
 *
 * 역할 분리:
 * - getSelectedSize()가 콤보박스 문자열을 LockerSize enum으로 변환하여 반환한다.
 *   Controller는 타입이 보장된 LockerSize만 다루므로 문자열 오타 버그가 사라진다.
 * - showSuccess()와 showError()를 분리하여 성공·실패 결과 포맷을 View가 직접 결정한다.
 *   Controller가 문자열을 조립하지 않는다.
 */
public class DepositView extends JFrame {

    private JTextField recipientField;   // 수령인 이름 입력창
    private JComboBox<String> sizeCombo; // 칸 크기 선택 (소형/중형/대형)
    private JButton depositButton;       // 보관 버튼
    private JLabel resultLabel;          // 결과 메시지 출력 (인증코드, 오류 등)

    public DepositView() {
        setTitle("택배 보관");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("택배 보관", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel recipientLabel = new JLabel("수령인 이름");
        recipientField = new JTextField(15);

        JLabel sizeLabel = new JLabel("보관함 크기");
        String[] sizes = {"소형", "중형", "대형"};
        sizeCombo = new JComboBox<String>(sizes);

        inputPanel.add(recipientLabel);
        inputPanel.add(recipientField);
        inputPanel.add(sizeLabel);
        inputPanel.add(sizeCombo);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        depositButton = new JButton("보관하기");
        depositButton.setFont(new Font("Dialog", Font.BOLD, 18));

        resultLabel = new JLabel("보관 결과가 여기에 표시됩니다.", SwingConstants.CENTER);

        bottomPanel.add(depositButton);
        bottomPanel.add(resultLabel);

        c.add(titleLabel, BorderLayout.NORTH);
        c.add(inputPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        setSize(420, 280);
        setVisible(true);
    }

    /**
     * 사용자가 입력한 수령인 이름을 반환한다.
     *
     * @return 수령인 이름 문자열
     */
    public String getRecipient() {
        return recipientField.getText();
    }

    /**
     * 사용자가 선택한 칸 크기를 LockerSize enum으로 변환하여 반환한다.
     * 문자열 → enum 변환 책임을 View가 가지므로,
     * Controller는 타입이 보장된 LockerSize만 다루면 된다.
     *
     * @return 선택된 LockerSize
     */
    public LockerSize getSelectedSize() {
        return LockerSize.fromLabel((String) sizeCombo.getSelectedItem());
    }

    /**
     * 보관 성공 결과를 화면에 표시한다.
     * 칸 번호와 인증코드를 받아 포맷을 View가 직접 결정한다.
     *
     * @param lockerId 배정된 칸 번호
     * @param authCode 발급된 인증코드
     */
    public void showSuccess(String lockerId, String authCode) {
        String message = "보관 완료! 칸 번호: " + lockerId
                + "<br>인증코드: " + authCode
                + "<br>수령 시 인증코드를 반드시 기억해주세요.";
        resultLabel.setText("<html>" + message + "</html>");
    }

    /**
     * 보관 실패 오류 메시지를 화면에 표시한다.
     *
     * @param message 표시할 오류 메시지
     */
    public void showError(String message) {
        resultLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
    }

    /**
     * 보관 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addDepositListener(ActionListener listener) {
        depositButton.addActionListener(listener);
    }
}
