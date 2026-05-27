package view;

import javax.swing.*;
import java.awt.*;

/**
 * 프로그램 시작 시 가장 먼저 표시되는 메인 메뉴 화면.
 * 보관 / 수령 / 내 보관 내역 / 관리자 모드 버튼을 제공한다.
 */
public class MainMenuView extends JFrame {

    private JButton depositButton;    // 택배 보관 버튼
    private JButton pickupButton;     // 택배 수령 버튼
    private JButton historyButton;    // 보관 내역 조회 버튼
    private JButton adminButton;      // 관리자 모드 버튼

    public MainMenuView() {
        // TODO: UI 초기화 (setTitle, setSize, setLayout 등)
    }

    /**
     * 보관 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addDepositListener(java.awt.event.ActionListener listener) {
        depositButton.addActionListener(listener);
    }

    /**
     * 수령 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addPickupListener(java.awt.event.ActionListener listener) {
        pickupButton.addActionListener(listener);
    }

    /**
     * 보관 내역 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addHistoryListener(java.awt.event.ActionListener listener) {
        historyButton.addActionListener(listener);
    }

    /**
     * 관리자 모드 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addAdminListener(java.awt.event.ActionListener listener) {
        adminButton.addActionListener(listener);
    }
}
