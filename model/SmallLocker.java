package model;

/**
 * 소형 칸. Locker를 상속받아 소형 크기 정보를 제공한다.
 * 소형 택배(서류, 소형 상자 등) 보관에 사용된다.
 */
public class SmallLocker extends Locker {

    public SmallLocker(String lockerId) {
        super(lockerId, "소형");
    }

    @Override
    public String getSizeDescription() {
        return "소형 (가로 20cm 이하)";
    }
}
