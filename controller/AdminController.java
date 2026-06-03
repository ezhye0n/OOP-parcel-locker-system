package controller;

import model.Locker;
import model.LockerRepository;
import view.AdminView;

import java.util.List;

/**
 * 관리자 기능을 처리하는 Controller.
 * 전체 칸 현황 조회 및 만료 택배 강제 해제를 담당한다.
 *
 * MVC 역할 분리 원칙:
 * Controller는 데이터 흐름과 검증 로직만 담당하고,
 * 화면 표시 방식은 AdminView가 결정한다.
 *
 * 동기화 전략:
 * LockerRepository의 public 메서드가 이미 synchronized로 선언되어 있으므로,
 * Controller에서 별도의 synchronized 블록을 추가하지 않는다.
 */
public class AdminController {

    private final LockerRepository lockerRepository;
    private final AdminView adminView;

    /** 관리자 비밀번호 */
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
     * 선택된 칸을 강제로 비운다.
     * 만료 상태인 칸만 해제 가능하며, 그 외의 경우 View에 오류 메시지를 전달한다.
     * 처리 후 화면을 자동으로 갱신한다.
     *
     * @param lockerId 강제 해제할 칸의 ID
     */
    public void forceRelease(String lockerId) {
        // 선택된 칸이 없으면 안내
        if (lockerId == null) {
            adminView.showError("해제할 칸을 선택해주세요.");
            return;
        }

        Locker locker = lockerRepository.findById(lockerId);
        if (locker == null) return;

        // 만료 상태인 칸만 강제 해제 가능
        if (!locker.hasExpiredPackage()) {
            adminView.showError("만료된 택배가 있는 칸만 강제 해제할 수 있습니다.");
            return;
        }

        // 칸 해제 및 파일 저장
        locker.release();
        lockerRepository.save();

        // 처리 후 화면 자동 갱신
        loadLockerStatus();
    }

    /**
     * 만료 상태인 칸 목록만 필터링하여 반환한다.
     *
     * @return 만료된 Locker 리스트
     */
    public List<Locker> getExpiredList() {
        return lockerRepository.getExpiredLockers();
    }
}
