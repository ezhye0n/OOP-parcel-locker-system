package model;

import java.io.Serializable;

/**
 * 모든 택배함이 공통으로 가지는 상태와 동작을 정의하는 추상 클래스.
 *
 * <p>소형/중형/대형 택배함은 이 클래스를 상속하고, 크기별 설명은 getSizeDescription()을
 * 오버라이딩하여 다형적으로 제공한다.</p>
 *
 * <p>정렬을 위해 Comparable을 구현하여 칸 ID 기준 자연 순서를 제공한다(6주차 compareTo).
 * 또한 기존의 occupied(boolean) 필드를 제거하고 assignedPackage의 존재 여부로 사용 여부를
 * 파생시켜, 두 필드가 어긋나는 모순 상태를 원천 차단했다(단일 진실원천).</p>
 */
public abstract class Locker implements Serializable, Comparable<Locker> {

    private static final long serialVersionUID = 3L;

    private final String lockerId;       // 칸 고유 ID (예: S-01, M-03, L-05)
    private final LockerSize size;       // 칸 크기. 문자열 대신 enum으로 관리하여 타입 안전성을 높인다.
    private Package assignedPackage;      // 현재 배정된 택배. 비어 있으면 null (= 사용 중 여부의 단일 진실원천)

    protected Locker(String lockerId, LockerSize size) {
        this.lockerId = requireText(lockerId, "lockerId");
        if (size == null) {
            throw new IllegalArgumentException("size는 null일 수 없습니다.");
        }
        this.size = size;
        this.assignedPackage = null;
    }

    public String getLockerId() {
        return lockerId;
    }

    /** 기존 Controller/View와의 호환을 위해 한글 크기 문자열을 반환한다. */
    public String getSize() {
        return size.getLabel();
    }

    /** Model 내부 로직에서 사용할 타입 안전한 크기 값을 반환한다. */
    public LockerSize getLockerSize() {
        return size;
    }

    public Package getAssignedPackage() {
        return assignedPackage;
    }

    /** 별도 boolean 필드 없이 배정된 택배 유무로 사용 중 여부를 판단한다. */
    public boolean isOccupied() {
        return assignedPackage != null;
    }

    /** 비어 있으면 true, 이미 사용 중이면 false. */
    public boolean isAvailable() {
        return assignedPackage == null;
    }

    /**
     * 칸에 Package를 배정한다. 이미 사용 중인 칸에는 배정할 수 없게 하여 무결성을 보장한다.
     */
    public void assign(Package pkg) {
        if (pkg == null) {
            throw new IllegalArgumentException("배정할 Package는 null일 수 없습니다.");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("이미 사용 중인 택배함입니다: " + lockerId);
        }
        if (!pkg.isStored()) {
            throw new IllegalStateException("보관 가능한 상태의 택배만 배정할 수 있습니다: " + pkg.getStatusText());
        }
        this.assignedPackage = pkg;
    }

    /** 칸을 비어 있는 상태로 초기화한다(수령 완료 또는 관리자 처리 시). */
    public void release() {
        this.assignedPackage = null;
    }

    /** 이 칸의 택배가 만료 상태인지 반환한다. */
    public boolean hasExpiredPackage() {
        return assignedPackage != null && assignedPackage.isExpired();
    }

    /** 관리자 화면 등에 표시할 현재 상태 문자열을 반환한다. */
    public String getStatusText() {
        if (assignedPackage == null) {
            return "비어있음";
        }
        return assignedPackage.getStatusText();
    }

    /** 칸 크기 설명을 반환한다. 하위 클래스가 구체적인 기준을 제공한다(다형성). */
    public abstract String getSizeDescription();

    /** 칸 ID 기준 자연 순서(6주차 Comparable). */
    @Override
    public int compareTo(Locker other) {
        return this.lockerId.compareTo(other.lockerId);
    }

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
