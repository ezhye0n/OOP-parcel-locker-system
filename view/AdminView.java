package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 관리자 전용 화면.
 * 전체 보관함 현황을 표시하고 새로고침 기능을 제공한다.
 * 보관함별 상세 현황은 여러 줄 텍스트로 출력한다.
 */
public class AdminView extends JFrame {

    private JLabel totalLabel;       // 전체 보관함 수 표시
    private JLabel usedLabel;        // 사용 중 보관함 수 표시
    private JLabel emptyLabel;       // 빈 보관함 수 표시
    private JLabel expiredLabel;     // 만료 보관함 수 표시

    private JTextArea statusArea;    // 보관함별 상세 현황 출력
    private JButton refreshButton;   // 현황 새로고침 버튼
    private JButton backButton;      // 뒤로가기 버튼

    public AdminView() {
        // 프레임 기본 설정
        setTitle("관리자 모드");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 컨텐트팬을 가져와 배치관리자 설정
        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        // 제목 영역
        JLabel titleLabel = new JLabel("관리자 현황판", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // 요약 정보 영역
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        totalLabel = new JLabel("전체 보관함 수: 0", SwingConstants.CENTER);
        usedLabel = new JLabel("사용 중: 0", SwingConstants.CENTER);
        emptyLabel = new JLabel("빈 보관함: 0", SwingConstants.CENTER);
        expiredLabel = new JLabel("만료 보관함: 0", SwingConstants.CENTER);

        summaryPanel.add(totalLabel);
        summaryPanel.add(usedLabel);
        summaryPanel.add(emptyLabel);
        summaryPanel.add(expiredLabel);

        // 제목 + 요약 정보를 위쪽에 배치
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(titleLabel);
        topPanel.add(summaryPanel);

        // 상세 현황 출력 영역
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setText("보관함 상세 현황이 여기에 표시됩니다.");

        JScrollPane scrollPane = new JScrollPane(statusArea);

        // 버튼 영역
        refreshButton = new JButton("새로고침");
        backButton = new JButton("뒤로가기");

        refreshButton.setFont(new Font("Dialog", Font.BOLD, 15));
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 프레임에 컴포넌트 추가
        c.add(topPanel, BorderLayout.NORTH);
        c.add(scrollPane, BorderLayout.CENTER);
        c.add(buttonPanel, BorderLayout.SOUTH);

        // 프레임 크기 설정 및 화면 출력
        setSize(600, 450);
        setVisible(true);
    }

    /**
     * 관리자 화면 상단의 요약 정보를 갱신한다.
     *
     * @param total 전체 보관함 수
     * @param used 사용 중 보관함 수
     * @param empty 빈 보관함 수
     * @param expired 만료 보관함 수
     */
    public void updateSummary(int total, int used, int empty, int expired) {
        totalLabel.setText("전체 보관함 수: " + total);
        usedLabel.setText("사용 중: " + used);
        emptyLabel.setText("빈 보관함: " + empty);
        expiredLabel.setText("만료 보관함: " + expired);
    }

    /**
     * 보관함별 상세 현황을 화면에 표시한다.
     *
     * @param statusText 보관함 상세 현황 문자열
     */
    public void updateStatusArea(String statusText) {
        statusArea.setText(statusText);
    }

    /**
     * 새로고침 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addRefreshListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    /**
     * 뒤로가기 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
