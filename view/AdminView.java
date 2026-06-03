package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * 관리자 전용 화면.
 * 전체 칸 현황을 JTable로 표시하고 새로고침 기능을 제공한다.
 * Controller로부터 데이터 배열을 받아 테이블에 표시한다.
 * 화면 표시 방식(JTable 구성)은 View가 전적으로 담당한다.
 */
public class AdminView extends JFrame {

    private JLabel totalLabel;        // 전체 보관함 수 표시
    private JLabel usedLabel;         // 사용 중 보관함 수 표시
    private JLabel emptyLabel;        // 빈 보관함 수 표시
    private JLabel expiredLabel;      // 만료 보관함 수 표시

    private JTable lockerTable;           // 전체 칸 현황 테이블
    private DefaultTableModel tableModel; // 테이블 데이터 모델
    private JButton refreshButton;        // 현황 새로고침 버튼
    private JButton backButton;           // 뒤로가기 버튼

    /** 테이블 컬럼 헤더 */
    private static final String[] COLUMNS = {"칸 번호", "크기", "수령인", "보관일", "상태"};

    public AdminView() {
        // 프레임 기본 설정
        setTitle("관리자 모드");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        // 제목 영역
        JLabel titleLabel = new JLabel("관리자 현황판", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // 요약 정보 영역
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        totalLabel   = new JLabel("전체 보관함 수: 0", SwingConstants.CENTER);
        usedLabel    = new JLabel("사용 중: 0", SwingConstants.CENTER);
        emptyLabel   = new JLabel("빈 보관함: 0", SwingConstants.CENTER);
        expiredLabel = new JLabel("만료 보관함: 0", SwingConstants.CENTER);

        summaryPanel.add(totalLabel);
        summaryPanel.add(usedLabel);
        summaryPanel.add(emptyLabel);
        summaryPanel.add(expiredLabel);

        // 제목 + 요약 정보를 위쪽에 배치
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(titleLabel);
        topPanel.add(summaryPanel);

        // JTable 영역
        // DefaultTableModel을 사용하여 데이터를 동적으로 업데이트할 수 있다.
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            // 셀 편집 비활성화: 관리자 화면은 조회 전용이다.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lockerTable = new JTable(tableModel);
        lockerTable.setRowHeight(24);
        lockerTable.getTableHeader().setReorderingAllowed(false); // 컬럼 순서 변경 비활성화
        JScrollPane scrollPane = new JScrollPane(lockerTable);

        // 버튼 영역
        refreshButton = new JButton("새로고침");
        backButton    = new JButton("뒤로가기");

        refreshButton.setFont(new Font("Dialog", Font.BOLD, 15));
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 프레임에 컴포넌트 추가
        c.add(topPanel,     BorderLayout.NORTH);
        c.add(scrollPane,   BorderLayout.CENTER);
        c.add(buttonPanel,  BorderLayout.SOUTH);

        setSize(650, 450);
        setVisible(true);
    }

    /**
     * 관리자 화면 상단의 요약 정보를 갱신한다.
     * Controller가 집계한 카운트를 받아 각 레이블에 표시한다.
     *
     * @param total   전체 보관함 수
     * @param used    사용 중 보관함 수
     * @param empty   빈 보관함 수
     * @param expired 만료 보관함 수
     */
    public void updateSummary(int total, int used, int empty, int expired) {
        totalLabel.setText("전체 보관함 수: " + total);
        usedLabel.setText("사용 중: " + used);
        emptyLabel.setText("빈 보관함: " + empty);
        expiredLabel.setText("만료 보관함: " + expired);
    }

    /**
     * 전체 칸 현황 데이터를 테이블에 표시한다.
     * 기존 데이터를 초기화하고 새 데이터로 채운다.
     * 데이터를 어떻게 표시할지(JTable)는 View가 결정한다.
     *
     * @param data Controller에서 전달받은 2차원 데이터 배열
     */
    public void updateTable(Object[][] data) {
        // 기존 테이블 데이터 초기화
        tableModel.setRowCount(0);

        // 새 데이터 행 추가
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
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
