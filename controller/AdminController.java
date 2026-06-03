package controller;

import model.Locker;
import model.Package;
import model.LockerRepository;
import view.AdminView;

import java.util.List;

/**
 * 관리자 기능을 처리하는 Controller.
 * 전체 칸 현황을 집계하여 AdminView에 전달한다.
 *
 * MVC 역할 분리 원칙:
 * Controller는 데이터 집계와 배열 변환만 담당하고,
 * 화면 표시 방식(JTable 구성, 셀 편집 여부 등)은 AdminView가 결정한다.
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
     * 전체 칸 현황을 집계하여 AdminView에 전달한다.
     *
     * 처리 순서:
     * 1. 전체/사용 중/빈 칸/만료 개수를 집계하여 updateSummary()로 전달
     * 2. 각 칸의 데이터를 2차원 배열로 변환하여 updateTable()로 전달
     *
     * Controller는 데이터 배열만 만들고,
     * 이를 어떻게 보여줄지(JTable)는 AdminView가 결정한다.
     */
    public void loadLockerStatus() {
        List<Locker> allLockers = lockerRepository.getAllLockers();

        // 현황 카운트 집계
        int totalCount   = allLockers.size();
        int occupiedCount = 0;
        int emptyCount   = 0;
        int expiredCount = 0;

        // 테이블에 표시할 데이터 배열 생성
        // 컬럼 순서: 칸 번호 / 크기 / 수령인 / 보관일 / 상태
        Object[][] tableData = new Object[totalCount][5];

        for (int i = 0; i < allLockers.size(); i++) {
            Locker locker = allLockers.get(i);
            tableData[i][0] = locker.getLockerId();
            tableData[i][1] = locker.getSizeDescription();

            if (!locker.isOccupied()) {
                // 빈 칸
                emptyCount++;
                tableData[i][2] = "-";
                tableData[i][3] = "-";
                tableData[i][4] = "비어있음";
            } else {
                Package pkg = locker.getAssignedPackage();
                if (pkg != null && pkg.isExpired()) {
                    // 만료된 칸
                    expiredCount++;
                    occupiedCount++;
                    tableData[i][2] = pkg.getRecipient();
                    tableData[i][3] = pkg.getStoredAt().toLocalDate().toString();
                    tableData[i][4] = "만료";
                } else if (pkg != null) {
                    // 사용 중인 칸
                    occupiedCount++;
                    tableData[i][2] = pkg.getRecipient();
                    tableData[i][3] = pkg.getStoredAt().toLocalDate().toString();
                    tableData[i][4] = "사용 중";
                }
            }
        }

        // 요약 정보 View에 전달
        adminView.updateSummary(totalCount, occupiedCount, emptyCount, expiredCount);

        // 테이블 데이터 View에 전달 — 표시 방식은 AdminView가 결정
        adminView.updateTable(tableData);
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
