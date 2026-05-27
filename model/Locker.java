package model;

/**
 * 택배함 칸을 나타내는 추상 클래스.
 * SmallLocker, MediumLocker, LargeLocker가 이 클래스를 상속받아 구현한다.
 * 칸 번호, 크기, 사용 상태, 배정된 Package를 관리한다.
 */
public abstract class Locker {

    private final String lockerId;       // 칸 고유 ID (예: "S-01", "M-03")
    private final String size;           // 칸 크기 ("소형" / "중형" / "대형")
    private boolean isOccupied;          // 사용 중 여부
    private Package assignedPackage;     // 현재 배정된 Package (없으면 null)

    /**
     * @param lockerId 칸 고유 ID
     * @param size     칸 크기
     */
    public Locker(String lockerId, String size) {
        this.lockerId = lockerId;
        this.size = size;
        this.isOccupied = false;
        this.assignedPackage = null;
    }

    public String getLockerId() { return lockerId; }
    public String getSize() { return size; }
    public boolean isOccupied() { return isOccupied; }
    public Package getAssignedPackage() { return assignedPackage; }

    /**
     * 칸에 Package를 배정하고 상태를 "사용 중"으로 변경한다.
     *
     * @param pkg 배정할 Package
     */
    public void assign(Package pkg) {
        // TODO: 구현 예정
    }

    /**
     * 칸을 비우고 상태를 "비어있음"으로 초기화한다.
     */
    public void release() {
        // TODO: 구현 예정
    }

    /**
     * 칸 크기 설명을 반환하는 추상 메서드.
     * 각 하위 클래스에서 구체적인 크기 정보를 반환한다.
     *
     * @return 칸 크기 설명 문자열
     */
    public abstract String getSizeDescription();
}
