package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 모든 택배함이 공통으로 가지는 상태와 동작을 정의하는 추상 클래스.
 *
 * <p>Model 계층의 핵심 객체로서 칸 번호, 크기, 사용 여부, 배정된 택배를 관리한다.
 * 소형/중형/대형 택배함은 이 클래스를 상속하고, 크기별 설명은 각 하위 클래스가
 * 다형적으로 제공한다.</p>
 */
public abstract class Locker implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String lockerId;       // 칸 고유 ID (예: S-01, M-03, L-05)
    private final LockerSize size;       // 칸 크기. 문자열 대신 enum으로 관리하여 무결성을 높인다.
    private boolean occupied;            // 사용 중 여부
    private Package assignedPackage;     // 현재 배정된 택배. 비어 있으면 null

    /**
     * @param lockerId 칸 고유 ID
     * @param size     칸 크기 enum
     */
    protected Locker(String lockerId, LockerSize size) {
        this.lockerId = requireText(lockerId, "lockerId");
        this.size = Objects.requireNonNull(size, "size는 null일 수 없습니다.");
        this.occupied = false;
        this.assignedPackage = null;
    }

    public String getLockerId() {
        return lockerId;
    }

    /**
     * 기존 Controller/View와의 호환을 위해 한글 크기 문자열을 반환한다.
     *
     * @return 소형, 중형, 대형 중 하나
     */
    public String getSize() {
        return size.getLabel();
    }

    /**
     * Model 내부 로직에서 사용할 수 있는 타입 안전한 크기 값을 반환한다.
     *
     * @return LockerSize enum
     */
    public LockerSize getLockerSize() {
        return size;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Package getAssignedPackage() {
        return assignedPackage;
    }

    /**
     * 현재 칸에 새 택배를 배정할 수 있는지 반환한다.
     * Controller가 내부 필드 조합을 직접 판단하지 않도록 Model이 상태 규칙을 가진다.
     *
     * @return 비어 있으면 true, 이미 사용 중이면 false
     */
    public boolean isAvailable() {
        return !occupied && assignedPackage == null;
    }

    /**
     * 칸에 Package를 배정하고 상태를 사용 중으로 변경한다.
     * 이미 사용 중인 칸에는 새 택배를 배정할 수 없게 하여 데이터 무결성을 보장한다.
     *
     * @param pkg 배정할 Package
     */
    public void assign(Package pkg) {
        Objects.requireNonNull(pkg, "배정할 Package는 null일 수 없습니다.");

        if (!isAvailable()) {
            throw new IllegalStateException("이미 사용 중인 택배함입니다: " + lockerId);
        }
        if (!pkg.isStored()) {
            throw new IllegalStateException("보관 가능한 상태의 택배만 배정할 수 있습니다: " + pkg.getStatusText());
        }

        this.assignedPackage = pkg;
        this.occupied = true;
    }

    /**
     * 칸을 비어 있는 상태로 초기화한다.
     * 수령 완료 또는 관리자 강제 해제 시 호출된다.
     */
    public void release() {
        this.assignedPackage = null;
        this.occupied = false;
    }

    /**
     * 이 칸에 들어 있는 택배가 만료 상태인지 반환한다.
     *
     * @return 사용 중이고 배정된 택배가 만료되었으면 true
     */
    public boolean hasExpiredPackage() {
        return occupied && assignedPackage != null && assignedPackage.isExpired();
    }

    /**
     * 이 칸에 들어 있는 택배가 보관 기간을 초과했는지 반환한다.
     *
     * @param maxStorageDays 최대 보관 일수
     * @return 보관 기간을 초과한 택배가 있으면 true
     */
    public boolean hasOverduePackage(int maxStorageDays) {
        return occupied
            && assignedPackage != null
            && assignedPackage.isOverStorageLimit(maxStorageDays);
    }

    /**
     * 관리자 화면 등에 표시할 현재 상태 문자열을 반환한다.
     *
     * @return 비어있음, 만료, 수령 완료, 사용 중 중 하나
     */
    public String getStatusText() {
        if (isAvailable()) {
            return "비어있음";
        }
        if (assignedPackage == null) {
            return "상태 오류";
        }
        return assignedPackage.getStatusText();
    }

    /**
     * 칸 크기 설명을 반환한다.
     * 하위 클래스가 구체적인 크기 기준을 제공한다.
     *
     * @return 크기 설명 문자열
     */
    public abstract String getSizeDescription();

    protected static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "는 비어 있을 수 없습니다.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return lockerId + " / " + getSize() + " / " + getStatusText();
    }
}
