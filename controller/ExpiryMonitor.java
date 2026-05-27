package controller;

import model.LockerRepository;

/**
 * 만료 택배를 감지하는 백그라운드 스레드.
 * 프로그램 실행과 동시에 시작되며, CHECK_INTERVAL_MS마다
 * 전체 칸을 순회하여 3일 초과 택배를 만료 상태로 전환한다.
 *
 * 동기화 전략:
 * DepositController와 동시에 칸 데이터에 접근할 수 있으므로,
 * lockerRepository를 공유 락으로 사용하여 데이터 충돌을 방지한다.
 */
public class ExpiryMonitor implements Runnable {

    private final LockerRepository lockerRepository;
    private volatile boolean stopped = false;

    /** 만료 감지 주기 (1분) */
    private static final long CHECK_INTERVAL_MS = 60_000;

    public ExpiryMonitor(LockerRepository lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    /**
     * 스레드 진입점.
     * stopped 플래그가 true가 될 때까지 주기적으로 만료 여부를 감지한다.
     */
    @Override
    public void run() {
        while (!stopped) {
            synchronized (lockerRepository) {
                checkExpiry();
            }
            try {
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                // 인터럽트 발생 시 스레드를 안전하게 종료한다
                stopped = true;
            }
        }
    }

    /**
     * 전체 Package를 순회하여 보관 시각 기준 3일 초과 항목을 만료 상태로 전환한다.
     * 반드시 synchronized 블록 안에서 호출해야 한다.
     */
    private void checkExpiry() {
        // TODO: 구현 예정
    }

    /**
     * 스레드 종료를 요청한다.
     * 현재 주기가 끝난 후 다음 루프에서 종료된다.
     */
    public void stop() {
        this.stopped = true;
    }
}