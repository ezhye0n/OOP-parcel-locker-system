package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 칸 데이터를 관리하고 파일 I/O를 담당하는 클래스.
 * Map<String, Locker>로 전체 칸을 관리하며,
 * 프로그램 시작 시 파일에서 데이터를 불러오고
 * 변경 시마다 파일에 저장한다.
 *
 * 동기화 주의:
 * DepositController와 ExpiryMonitor가 이 객체를 공유 락으로 사용한다.
 * 칸 데이터 접근 시 반드시 synchronized(lockerRepository) 블록 안에서 호출할 것.
 */
public class LockerRepository {

    /** 전체 칸 데이터. 키: lockerId, 값: Locker 객체 */
    private final Map<String, Locker> lockers = new HashMap<>();

    /** 데이터 저장 파일 경로 */
    private static final String FILE_PATH = "lockers.dat";

    /**
     * 저장 파일에서 칸 데이터를 불러온다.
     * 파일이 없으면 빈 상태로 초기화한다.
     */
    public void load() {
        // TODO: 구현 예정
    }

    /**
     * 현재 칸 데이터를 파일에 저장한다.
     * 보관·수령·만료 처리 후 반드시 호출해야 한다.
     */
    public void save() {
        // TODO: 구현 예정
    }

    /**
     * 특정 크기의 빈 칸 목록을 반환한다.
     *
     * @param size 칸 크기 ("소형" / "중형" / "대형")
     * @return 해당 크기의 빈 Locker 리스트
     */
    public List<Locker> getAvailableLockers(String size) {
        // TODO: 구현 예정
        return new ArrayList<>();
    }

    /**
     * 전체 칸 목록을 반환한다.
     *
     * @return 전체 Locker 리스트
     */
    public List<Locker> getAllLockers() {
        return new ArrayList<>(lockers.values());
    }

    /**
     * ID로 특정 칸을 찾아 반환한다.
     *
     * @param lockerId 찾을 칸의 ID
     * @return 해당 Locker, 없으면 null
     */
    public Locker findById(String lockerId) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 만료 상태인 칸 목록을 반환한다.
     *
     * @return 만료된 Locker 리스트
     */
    public List<Locker> getExpiredLockers() {
        // TODO: 구현 예정
        return new ArrayList<>();
    }
}
