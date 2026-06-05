package model;

/**
 * 중형 택배함. 일반적인 크기의 택배 상자 보관에 사용된다.
 */
public class MediumLocker extends Locker {

    private static final long serialVersionUID = 3L;

    public MediumLocker(String lockerId) {
        super(lockerId, LockerSize.MEDIUM);
    }

    @Override
    public String getSizeDescription() {
        return LockerSize.MEDIUM.getDescription();
    }
}
