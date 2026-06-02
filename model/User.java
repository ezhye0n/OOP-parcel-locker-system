package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 사용자 정보를 관리하는 Model 클래스.
 * 사용자 ID, 이름, 보관 내역 목록을 가진다.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String userId;                // 사용자 고유 ID
    private final String name;                  // 사용자 이름
    private final List<Package> packageHistory; // 사용자의 보관 내역

    /**
     * @param userId 사용자 고유 ID
     * @param name   사용자 이름
     */
    public User(String userId, String name) {
        this.userId = requireText(userId, "userId");
        this.name = requireText(name, "name");
        this.packageHistory = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    /**
     * 사용자의 보관 내역에 택배를 추가한다.
     * null 값이 들어가지 못하게 하여 내역 데이터의 무결성을 유지한다.
     *
     * @param pkg 추가할 Package
     */
    public void addPackage(Package pkg) {
        packageHistory.add(Objects.requireNonNull(pkg, "추가할 Package는 null일 수 없습니다."));
    }

    /**
     * 전체 보관 내역을 반환한다.
     * 내부 리스트를 그대로 노출하지 않고 수정 불가능한 복사본을 반환한다.
     *
     * @return 보관 내역 리스트
     */
    public List<Package> getPackageHistory() {
        return Collections.unmodifiableList(new ArrayList<>(packageHistory));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "는 비어 있을 수 없습니다.");
        }
        return value.trim();
    }
}
