package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 사용자 정보를 관리하는 클래스.
 * 사용자 ID, 이름, 보관 내역 목록을 관리한다.
 */
public class User {

    private final String userId;                // 사용자 고유 ID
    private final String name;                  // 사용자 이름
    private final List<Package> packageHistory; // 보관 내역 목록

    /**
     * @param userId 사용자 고유 ID
     * @param name   사용자 이름
     */
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.packageHistory = new ArrayList<>();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }

    /**
     * 보관 내역에 Package를 추가한다.
     *
     * @param pkg 추가할 Package
     */
    public void addPackage(Package pkg) {
        // TODO: 구현 예정
    }

    /**
     * 전체 보관 내역을 반환한다.
     * 외부에서 리스트를 직접 수정하지 못하도록 수정 불가 복사본을 반환한다.
     *
     * @return 보관 내역 리스트 (수정 불가)
     */
    public List<Package> getPackageHistory() {
        // TODO: 구현 예정
        return Collections.unmodifiableList(packageHistory);
    }
}
