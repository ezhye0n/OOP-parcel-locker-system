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
 * ADMIN_PASSWORD가 현재 소스코드에 하드코딩되어 있다.
 * 실제 운영 환경에서는 소스코드에 비밀번호를 직접 작성하면 안 된다.
 * 환경 변수(System.getenv) 또는 외부 설정 파일에서 읽어오는 방식으로 교체해야 한다.
 * 예: String pw = System.getenv("ADMIN_PASSWORD");
 */
public class AdminController {

    private final LockerRepository lockerRepository;
    private final AdminView adminView;

    /**
     * 관리자 비밀번호.
     * TODO: 운영 환경에서는 System.getenv("ADMIN_PASSWORD") 등으로 교체할 것.
     */
    private static final String ADMIN_PASSWORD = "admin1234";

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
