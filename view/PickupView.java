package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 택배 수령 화면.
 * 6자리 인증코드를 입력받아 수령을 처리하고 결과를 표시한다.
 */
public class PickupView extends JFrame {

    private JTextField authCodeField; // 인증코드 입력창
    private JButton pickupButton;     // 수령 버튼
    private JLabel resultLabel;       // 결과 메시지 출력 (성공/실패)

    public PickupView() {
        // 프레임 기본 설정
        setTitle("택배 수령");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 컨텐트팬을 가져와 배치관리자 설정
        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        // 제목 영역
        JLabel titleLabel = new JLabel("택배 수령", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // 입력 영역
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JLabel authCodeLabel = new JLabel("인증코드 입력");
        authCodeField = new JTextField(10);

        inputPanel.add(authCodeLabel);
        inputPanel.add(authCodeField);

        // 버튼과 결과 메시지 영역
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        pickupButton = new JButton("수령하기");
        pickupButton.setFont(new Font("Dialog", Font.BOLD, 18));

        resultLabel = new JLabel("수령 결과가 여기에 표시됩니다.", SwingConstants.CENTER);

        bottomPanel.add(pickupButton);
        bottomPanel.add(resultLabel);

        // 프레임에 컴포넌트 추가
        c.add(titleLabel, BorderLayout.NORTH);
        c.add(inputPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 크기 설정 및 화면 출력
        setSize(400, 240);
        setVisible(true);
    }

    /**
     * 사용자가 입력한 인증코드를 반환한다.
     *
     * @return 6자리 인증코드 문자열
     */
    public String getAuthCode() {
        return authCodeField.getText();
    }

    /**
     * 수령 결과 메시지를 화면에 표시한다.
     * 성공 시 "수령 완료", 실패 시 오류 원인을 표시한다.
     *
     * @param message 표시할 메시지
     */
    public void showResult(String message) {
    resultLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
    }

    /**
     * 수령 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addPickupListener(ActionListener listener) {
        pickupButton.addActionListener(listener);
    }
}
