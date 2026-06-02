package model;

/**
 * 소형 택배함.
 * 서류나 작은 상자처럼 부피가 작은 택배 보관에 사용된다.
 */
public class SmallLocker extends Locker {

    private static final long serialVersionUID = 2L;

    public SmallLocker(String lockerId) {
        super(lockerId, LockerSize.SMALL);
    }

    @Override
    public String getSizeDescription() {
        return LockerSize.SMALL.getDescription();
    }
}
