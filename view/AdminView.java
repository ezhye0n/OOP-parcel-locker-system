package view;

import model.Locker;
import model.Package;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 관리자 전용 화면.
 * 전체 칸 현황을 JTable로 표시하고 새로고침 기능을 제공한다.
 *
 * MVC 역할 분리 원칙:
 * Controller로부터 List<Locker>를 받아 테이블 데이터로 변환하는 책임은 View가 가진다.
 * Controller는 데이터 흐름만 제어하고, 화면 표시 방식은 View가 결정한다.
 * 따라서 테이블 컬럼 구성이나 표시 형식이 바뀌어도 Controller를 수정하지 않아도 된다.
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

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(titleLabel);
        topPanel.add(summaryPanel);

        // JTable 영역
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            // 셀 편집 비활성화: 관리자 화면은 조회 전용이다.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lockerTable = new JTable(tableModel);
        lockerTable.setRowHeight(24);
        lockerTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(lockerTable);

        // 버튼 영역
        refreshButton = new JButton("새로고침");
        backButton    = new JButton("뒤로가기");

        refreshButton.setFont(new Font("Dialog", Font.BOLD, 15));
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        c.add(topPanel,    BorderLayout.NORTH);
        c.add(scrollPane,  BorderLayout.CENTER);
        c.add(buttonPanel, BorderLayout.SOUTH);

        setSize(650, 450);
        setVisible(true);
    }

    /**
     * Controller로부터 전달받은 Locker 리스트로 화면 전체를 갱신한다.
     * 요약 카운트 집계와 테이블 데이터 변환을 View가 직접 담당한다.
     * Controller는 List<Locker>만 넘기고 표시 방식은 View가 결정한다.
     *
     * @param lockers 전체 Locker 리스트
     */
    public void updateView(List<Locker> lockers) {
        // 요약 카운트 집계 — 표시 목적이므로 View가 담당
        int total    = lockers.size();
        int occupied = 0;
        int empty    = 0;
        int expired  = 0;

        // 기존 테이블 데이터 초기화
        tableModel.setRowCount(0);

        for (Locker locker : lockers) {
            // 카운트 집계
            if (!locker.isOccupied()) {
                empty++;
            } else if (locker.hasExpiredPackage()) {
                expired++;
                occupied++;
            } else {
                occupied++;
            }

            // 테이블 행 데이터 구성 — 화면 표시 형식은 View가 결정
            String recipient = "-";
            String storedDate = "-";

            if (locker.isOccupied() && locker.getAssignedPackage() != null) {
                Package pkg = locker.getAssignedPackage();
                recipient  = pkg.getRecipient();
                storedDate = pkg.getStoredAt().toLocalDate().toString();
            }

            // 상태 문자열은 Model(Locker)이 결정한다
            tableModel.addRow(new Object[]{
                locker.getLockerId(),
                locker.getSizeDescription(),
                recipient,
                storedDate,
                locker.getStatusText()
            });
        }

        // 요약 레이블 갱신
        totalLabel.setText("전체 보관함 수: " + total);
        usedLabel.setText("사용 중: " + occupied);
        emptyLabel.setText("빈 보관함: " + empty);
        expiredLabel.setText("만료 보관함: " + expired);
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
