package model;

/**
 * 대형 택배함.
 * 부피가 큰 택배 상자 보관에 사용된다.
 */
public class LargeLocker extends Locker {

    private static final long serialVersionUID = 1L;

    public LargeLocker(String lockerId) {
        super(lockerId, "대형");
    }

    @Override
    public String getSizeDescription() {
        return "대형 (가로 60cm 이하)";
    }
}
