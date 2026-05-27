package model;

/**
 * 중형 칸. Locker를 상속받아 중형 크기 정보를 제공한다.
 */
public class MediumLocker extends Locker {

    public MediumLocker(String lockerId) {
        super(lockerId, "중형");
    }

    @Override
    public String getSizeDescription() {
        return "중형 (가로 40cm 이하)";
    }
}
