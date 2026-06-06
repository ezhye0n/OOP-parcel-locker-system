package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        // 프레임 기본 설정
        setTitle("택배 보관");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 컨텐트팬을 가져와 배치관리자 설정
        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        // 제목 영역
        JLabel titleLabel = new JLabel("택배 보관", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // 입력 영역
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

        // 버튼과 결과 메시지 영역
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        depositButton = new JButton("보관하기");
        depositButton.setFont(new Font("Dialog", Font.BOLD, 18));

        resultLabel = new JLabel("보관 결과가 여기에 표시됩니다.", SwingConstants.CENTER);

        bottomPanel.add(depositButton);
        bottomPanel.add(resultLabel);

        // 프레임에 컴포넌트 추가
        c.add(titleLabel, BorderLayout.NORTH);
        c.add(inputPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 크기 설정 및 화면 출력
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
     * 사용자가 선택한 칸 크기를 반환한다.
     *
     * @return 칸 크기 ("소형" / "중형" / "대형")
     */
    public String getSelectedSize() {
        return (String) sizeCombo.getSelectedItem();
    }

    /**
     * 보관 결과 메시지를 화면에 표시한다.
     * 성공 시 인증코드와 칸 번호, 실패 시 오류 메시지를 표시한다.
     *
     * @param message 표시할 메시지
     */
    public void showResult(String message) {
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
