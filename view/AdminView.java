package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * 관리자 전용 화면.
 * 전체 칸 현황을 테이블로 표시하고 만료 택배 강제 처리 기능을 제공한다.
 * 진입 시 관리자 비밀번호 검증을 거친다.
 */
public class AdminView extends JFrame {

    private JTable lockerTable;           // 전체 칸 현황 테이블
    private DefaultTableModel tableModel; // 테이블 데이터 모델
    private JButton forceReleaseButton;   // 만료 택배 강제 처리 버튼
    private JButton refreshButton;        // 현황 새로고침 버튼

    /** 테이블 컬럼 헤더 */
    private static final String[] COLUMNS = {"칸 번호", "크기", "수령인", "보관 시각", "상태"};

    public AdminView() {
        // TODO: UI 초기화
    }

    /**
     * 전체 칸 현황 데이터를 테이블에 표시한다.
     *
     * @param data 테이블에 표시할 2차원 데이터 배열
     */
    public void updateTable(Object[][] data) {
        // TODO: 구현 예정
    }

    /**
     * 테이블에서 현재 선택된 칸의 ID를 반환한다.
     * 강제 처리 버튼 클릭 시 사용된다.
     *
     * @return 선택된 칸 ID, 선택 없으면 null
     */
    public String getSelectedLockerId() {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 강제 처리 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addForceReleaseListener(java.awt.event.ActionListener listener) {
        forceReleaseButton.addActionListener(listener);
    }

    /**
     * 새로고침 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addRefreshListener(java.awt.event.ActionListener listener) {
        refreshButton.addActionListener(listener);
    }
}
