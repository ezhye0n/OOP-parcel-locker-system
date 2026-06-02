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

    private static final long serialVersionUID = 1L;

    private final String lockerId;       // 칸 고유 ID (예: S-01, M-03, L-05)
    private final String size;           // 칸 크기 (소형, 중형, 대형)
    private boolean occupied;            // 사용 중 여부
    private Package assignedPackage;     // 현재 배정된 택배. 비어 있으면 null

    /**
     * @param lockerId 칸 고유 ID
     * @param size     칸 크기
     */
    protected Locker(String lockerId, String size) {
        this.lockerId = requireText(lockerId, "lockerId");
        this.size = requireText(size, "size");
        this.occupied = false;
        this.assignedPackage = null;
    }

    public String getLockerId() {
        return lockerId;
    }

    public String getSize() {
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "는 비어 있을 수 없습니다.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return lockerId + " / " + size + " / " + getStatusText();
    }
}
