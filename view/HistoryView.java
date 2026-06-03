package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * 보관 내역 조회 화면.
 * 사용자의 보관 내역을 테이블 형태로 표시한다.
 * 표시 항목: 칸 번호, 수령인, 보관 시각, 상태
 */
public class HistoryView extends JFrame {

    private JTable historyTable;             // 보관 내역 테이블
    private DefaultTableModel tableModel;    // 테이블 데이터 모델
    private JButton closeButton;             // 닫기 버튼

    /** 테이블 컬럼 헤더 */
    private static final String[] COLUMNS = {"칸 번호", "수령인", "보관 시각", "상태"};

    public HistoryView() {
        // TODO: UI 초기화
    }

    /**
     * 보관 내역 데이터를 테이블에 표시한다.
     * 기존 데이터를 초기화하고 새 데이터로 채운다.
     *
     * @param data 테이블에 표시할 2차원 데이터 배열
     */
    public void updateTable(Object[][] data) {
        // TODO: 구현 예정
    }
}
