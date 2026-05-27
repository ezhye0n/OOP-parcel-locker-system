package controller;

import model.Locker;
import model.LockerRepository;
import view.AdminView;
import java.util.List;

/**
 * 관리자 기능을 처리하는 Controller.
 * 전체 칸 현황 조회, 만료 택배 강제 처리 등
 * 일반 사용자에게는 노출되지 않는 관리 기능을 담당한다.
 */
public class AdminController {

    private final LockerRepository lockerRepository;
    private final AdminView adminView;

    public AdminController(LockerRepository lockerRepository, AdminView adminView) {
        this.lockerRepository = lockerRepository;
        this.adminView = adminView;
    }

    /**
     * 전체 칸 목록을 불러와 AdminView에 전달한다.
     * 사용 중 / 빈 칸 / 만료 상태를 구분하여 표시한다.
     */
    public void loadLockerStatus() {
        // TODO: 구현 예정
    }

    /**
     * 관리자가 선택한 만료 칸을 강제로 비운다.
     * 해당 칸의 Package를 제거하고 상태를 "비어있음"으로 초기화한다.
     *
     * @param lockerId 강제 처리할 칸의 ID
     */
    public void forceRelease(String lockerId) {
        // TODO: 구현 예정
    }

    /**
     * 만료 상태인 칸 목록만 필터링하여 반환한다.
     * AdminView의 만료 목록 표시에 사용된다.
     *
     * @return 만료된 Locker 리스트
     */
    public List<Locker> getExpiredList() {
        // TODO: 구현 예정
        return null;
    }
}