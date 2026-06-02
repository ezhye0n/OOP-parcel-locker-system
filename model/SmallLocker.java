package model;

/**
 * 소형 택배함.
 * 서류나 작은 상자처럼 부피가 작은 택배 보관에 사용된다.
 */
public class SmallLocker extends Locker {

    private static final long serialVersionUID = 1L;

    public SmallLocker(String lockerId) {
        super(lockerId, "소형");
    }

    @Override
    public String getSizeDescription() {
        return "소형 (가로 20cm 이하)";
    }
}
