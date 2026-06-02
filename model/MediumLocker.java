package model;

/**
 * 중형 택배함.
 * 일반적인 크기의 택배 상자 보관에 사용된다.
 */
public class MediumLocker extends Locker {

    private static final long serialVersionUID = 1L;

    public MediumLocker(String lockerId) {
        super(lockerId, "중형");
    }

    @Override
    public String getSizeDescription() {
        return "중형 (가로 40cm 이하)";
    }
}
