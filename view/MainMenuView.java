package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 프로그램 시작 시 가장 먼저 표시되는 메인 메뉴 화면.
 * 보관 / 수령 / 관리자 모드 버튼을 제공한다.
 */
public class MainMenuView extends JFrame {

    private JButton depositButton;    // 택배 보관 버튼
    private JButton pickupButton;     // 택배 수령 버튼
    private JButton adminButton;      // 관리자 모드 버튼

    public MainMenuView() {
        // 프레임 기본 설정
        setTitle("무인 택배함 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 컨텐트팬을 가져와 배치관리자 설정
        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        // 제목 영역
        JLabel titleLabel = new JLabel("무인 택배함 시스템", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        JLabel guideLabel = new JLabel("원하는 메뉴를 선택하세요.", SwingConstants.CENTER);
        guideLabel.setFont(new Font("Dialog", Font.PLAIN, 15));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.add(titleLabel);
        titlePanel.add(guideLabel);

        // 버튼 생성
        depositButton = new JButton("택배 보관");
        pickupButton = new JButton("택배 수령");
        adminButton = new JButton("관리자 모드");

        // 키오스크 느낌을 위해 버튼 글꼴을 크게 설정
        depositButton.setFont(new Font("Dialog", Font.BOLD, 18));
        pickupButton.setFont(new Font("Dialog", Font.BOLD, 18));
        adminButton.setFont(new Font("Dialog", Font.BOLD, 18));

        // 버튼들을 세로로 중앙 영역에 배치
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.add(depositButton);
        buttonPanel.add(pickupButton);
        buttonPanel.add(adminButton);

        // 프레임에 제목 영역과 버튼 영역 추가
        c.add(titlePanel, BorderLayout.NORTH);
        c.add(buttonPanel, BorderLayout.CENTER);

        // 프레임 크기 설정 및 화면 출력
        setSize(420, 320);
        setVisible(true);
    }

    /**
     * 보관 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addDepositListener(ActionListener listener) {
        depositButton.addActionListener(listener);
    }

    /**
     * 수령 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addPickupListener(ActionListener listener) {
        pickupButton.addActionListener(listener);
    }

    /**
     * 관리자 모드 버튼 클릭 시 실행할 동작을 Controller에서 등록한다.
     *
     * @param listener 버튼 클릭 이벤트 리스너
     */
    public void addAdminListener(ActionListener listener) {
        adminButton.addActionListener(listener);
    }
}
