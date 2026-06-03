import controller.AdminController;
import controller.DepositController;
import controller.ExpiryMonitor;
import controller.PickupController;
import model.LockerRepository;
import view.AdminView;
import view.DepositView;
import view.MainMenuView;
import view.PickupView;

import javax.swing.*;

/**
 * 무인 택배함 시스템의 진입점.
 *
 * 실행 순서:
 * 1. LockerRepository 생성 및 파일에서 데이터 불러오기
 * 2. ExpiryMonitor 백그라운드 스레드 시작
 * 3. MainMenuView 생성 및 각 버튼에 Controller 연결
 *
 * MVC 연결 구조:
 * Main이 View, Controller, Model을 생성하고 연결한다.
 * 각 레이어는 서로를 직접 생성하지 않아 결합도를 낮춘다.
 */
public class Main {

    public static void main(String[] args) {
        // Swing은 EDT(Event Dispatch Thread)에서 실행해야 한다
        SwingUtilities.invokeLater(() -> {
            // 1. Model 초기화: 파일에서 택배함 데이터 불러오기
            LockerRepository lockerRepository = new LockerRepository();
            lockerRepository.load();

            // 2. ExpiryMonitor 백그라운드 스레드 시작
            // 프로그램 종료 시 자동으로 종료되도록 데몬 스레드로 설정한다
            ExpiryMonitor expiryMonitor = new ExpiryMonitor(lockerRepository);
            Thread monitorThread = new Thread(expiryMonitor);
            monitorThread.setDaemon(true);
            monitorThread.start();

            // 3. MainMenuView 생성 및 버튼 이벤트 연결
            MainMenuView mainMenuView = new MainMenuView();

            // 보관 버튼: DepositView + DepositController 생성 후 연결
            mainMenuView.addDepositListener(e -> {
                DepositView depositView = new DepositView();
                DepositController depositController = new DepositController(lockerRepository, depositView);

                depositView.addDepositListener(depositEvent ->
                    depositController.handleDeposit(
                        depositView.getRecipient(),
                        depositView.getSelectedSize()
                    )
                );
            });

            // 수령 버튼: PickupView + PickupController 생성 후 연결
            mainMenuView.addPickupListener(e -> {
                PickupView pickupView = new PickupView();
                PickupController pickupController = new PickupController(lockerRepository, pickupView);

                pickupView.addPickupListener(pickupEvent ->
                    pickupController.handlePickup(pickupView.getAuthCode())
                );
            });

            // 관리자 모드 버튼: 비밀번호 확인 후 AdminView 열기
            mainMenuView.addAdminListener(e -> {
                String password = JOptionPane.showInputDialog(
                    mainMenuView,
                    "관리자 비밀번호를 입력하세요.",
                    "관리자 모드",
                    JOptionPane.PLAIN_MESSAGE
                );

                // 취소 버튼 또는 빈 입력 처리
                if (password == null || password.trim().isEmpty()) return;

                AdminView adminView = new AdminView();
                AdminController adminController = new AdminController(lockerRepository, adminView);

                // 비밀번호 검증
                if (!adminController.validateAdminPassword(password)) {
                    JOptionPane.showMessageDialog(
                        adminView,
                        "비밀번호가 올바르지 않습니다.",
                        "접근 거부",
                        JOptionPane.ERROR_MESSAGE
                    );
                    adminView.dispose();
                    return;
                }

                // 현황 초기 로드
                adminController.loadLockerStatus();

                // 새로고침 버튼: 현황 다시 불러오기
                adminView.addRefreshListener(refreshEvent ->
                    adminController.loadLockerStatus()
                );

                // 뒤로가기 버튼: AdminView 닫기
                adminView.addBackListener(backEvent ->
                    adminView.dispose()
                );
            });
        });
    }
}
