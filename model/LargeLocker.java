package model;

/**
 * 대형 택배함.
 * 부피가 큰 택배 상자 보관에 사용된다.
 */
public class LargeLocker extends Locker {

    private static final long serialVersionUID = 2L;

    public LargeLocker(String lockerId) {
        super(lockerId, LockerSize.LARGE);
    }

    @Override
    public String getSizeDescription() {
        return LockerSize.LARGE.getDescription();
    }
}
