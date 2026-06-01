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
     * 전체 칸 목록을 불러와 AdminView에 전달한다.
     * 사용 중 / 빈 칸 / 만료 상태를 구분하여 테이블 데이터로 변환한다.
     */
    public void loadLockerStatus() {
        List<Locker> allLockers = lockerRepository.getAllLockers();

        // 테이블에 표시할 데이터 배열 생성
        // 컬럼: 칸 번호 / 크기 / 수령인 / 보관 시각 / 상태
        Object[][] tableData = new Object[allLockers.size()][5];

        for (int i = 0; i < allLockers.size(); i++) {
            Locker locker = allLockers.get(i);
            tableData[i][0] = locker.getLockerId();
            tableData[i][1] = locker.getSizeDescription();

            if (locker.isOccupied() && locker.getAssignedPackage() != null) {
                tableData[i][2] = locker.getAssignedPackage().getRecipient();
                tableData[i][3] = locker.getAssignedPackage().getStoredAt().toString();
                tableData[i][4] = locker.getAssignedPackage().isExpired() ? "만료" : "사용 중";
            } else {
                tableData[i][2] = "-";
                tableData[i][3] = "-";
                tableData[i][4] = "비어있음";
            }
        }

        adminView.updateTable(tableData);
    }

    /**
     * 관리자가 선택한 만료 칸을 강제로 비운다.
     * 해당 칸의 Package를 제거하고 상태를 "비어있음"으로 초기화한다.
     * 이미 비어있는 칸을 선택하면 오류 메시지를 표시한다.
     *
     * @param lockerId 강제 처리할 칸의 ID
     */
    public void forceRelease(String lockerId) {
        if (lockerId == null) {
            return;
        }

        synchronized (lockerRepository) {
            Locker locker = lockerRepository.findById(lockerId);

            if (locker == null) return;

            // 이미 비어있는 칸은 처리하지 않음
            if (!locker.isOccupied()) {
                adminView.updateTable(getCurrentTableData());
                return;
            }

            // 칸 강제 해제
            locker.release();
            lockerRepository.save();
        }

        // 처리 후 화면 갱신
        loadLockerStatus();
    }

    /**
     * 만료 상태인 칸 목록만 필터링하여 반환한다.
     * AdminView의 만료 목록 표시에 사용된다.
     *
     * @return 만료된 Locker 리스트
     */
    public List<Locker> getExpiredList() {
        return lockerRepository.getExpiredLockers();
    }

    /**
     * 현재 전체 칸 상태를 테이블 데이터로 반환한다.
     * forceRelease 후 화면 갱신에 사용된다.
     *
     * @return 테이블 표시용 2차원 배열
     */
    private Object[][] getCurrentTableData() {
        List<Locker> allLockers = lockerRepository.getAllLockers();
        Object[][] tableData = new Object[allLockers.size()][5];

        for (int i = 0; i < allLockers.size(); i++) {
            Locker locker = allLockers.get(i);
            tableData[i][0] = locker.getLockerId();
            tableData[i][1] = locker.getSizeDescription();

            if (locker.isOccupied() && locker.getAssignedPackage() != null) {
                tableData[i][2] = locker.getAssignedPackage().getRecipient();
                tableData[i][3] = locker.getAssignedPackage().getStoredAt().toString();
                tableData[i][4] = locker.getAssignedPackage().isExpired() ? "만료" : "사용 중";
            } else {
                tableData[i][2] = "-";
                tableData[i][3] = "-";
                tableData[i][4] = "비어있음";
            }
        }
        return tableData;
    }
}
