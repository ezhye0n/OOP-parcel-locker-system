package controller;

import model.Locker;
import model.LockerRepository;
import view.AdminView;

import java.util.List;

/**
 * 관리자 기능을 처리하는 Controller.
 * 전체 칸 현황 조회를 담당한다.
 *
 * MVC 역할 분리 원칙:
 * Controller는 데이터 흐름만 제어하고,
 * 화면 표시 방식은 AdminView가 결정한다.
 *
 * 동기화 전략:
 * LockerRepository의 public 메서드가 이미 synchronized로 선언되어 있으므로,
 * Controller에서 별도의 synchronized 블록을 추가하지 않는다.
 *
 * 보안 주의:
 * ADMIN_PASSWORD는 환경변수(ADMIN_PASSWORD)에서 먼저 읽어온다.
 * 환경변수가 설정되지 않은 경우에만 기본값 "admin1234"를 사용한다.
 * 실제 운영 환경에서는 반드시 환경변수를 설정해야 한다.
 */
public class AdminController {

    private final LockerRepository lockerRepository;
    private final AdminView adminView;

    /**
     * 관리자 비밀번호.
     * 환경변수 ADMIN_PASSWORD가 설정되어 있으면 그 값을 사용하고,
     * 없으면 개발용 기본값 "admin1234"를 사용한다.
     */
    private static final String ADMIN_PASSWORD =
            System.getenv("ADMIN_PASSWORD") != null
            ? System.getenv("ADMIN_PASSWORD")
            : "admin1234";

    public AdminController(LockerRepository lockerRepository, AdminView adminView) {
        this.lockerRepository = lockerRepository;
        this.adminView = adminView;
    }

    /**
     * 관리자 비밀번호를 검증한다.
     * AdminView 진입 전 호출하여 권한을 확인한다.
     *
     * @param inputPassword 입력된 비밀번호
     * @return 비밀번호가 일치하면 true, 아니면 false
     */
    public boolean validateAdminPassword(String inputPassword) {
        return ADMIN_PASSWORD.equals(inputPassword);
    }

    /**
     * 전체 칸 목록을 Model에서 가져와 AdminView에 전달한다.
     * Controller는 데이터 흐름만 제어하고,
     * 데이터를 테이블에 어떻게 표시할지는 AdminView가 결정한다.
     */
    public void loadLockerStatus() {
        List<Locker> allLockers = lockerRepository.getAllLockers();
        adminView.updateView(allLockers);
    }

    /**
     * 만료 상태인 칸 목록만 필터링하여 반환한다.
     * 향후 만료 칸 별도 표시 기능 추가 시 사용할 "확장 포인트"로 정의해두었다.
     *
     * @return 만료된 Locker 리스트
     */
    public List<Locker> getExpiredList() {
        return lockerRepository.getExpiredLockers();
    }
}
