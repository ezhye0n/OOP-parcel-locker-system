package model;

/**
 * 대형 칸. Locker를 상속받아 대형 크기 정보를 제공한다.
 */
public class LargeLocker extends Locker {

    public LargeLocker(String lockerId) {
        super(lockerId, "대형");
    }

    @Override
    public String getSizeDescription() {
        return "대형 (가로 60cm 이하)";
    }
}
