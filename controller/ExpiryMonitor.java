package controller;

import model.LockerRepository;

/**
 * 만료 택배를 감지하는 백그라운드 스레드.
 * 프로그램 실행과 동시에 시작되며, CHECK_INTERVAL_MS마다
 * LockerRepository에 만료 처리를 위임한다.
 *
 * 동기화 전략:
 * LockerRepository의 markOverduePackagesAsExpired()가 이미 synchronized로
 * 선언되어 있으므로, Controller에서 별도로 synchronized 블록을 추가하지 않는다.
 * Model이 동기화 책임을 가지고, Controller는 흐름 제어만 담당한다.
 */
public class ExpiryMonitor implements Runnable {

    private final LockerRepository lockerRepository;

    // volatile: 메인 스레드가 stop()을 호출했을 때
    // ExpiryMonitor 스레드가 변경을 즉시 감지하도록 보장한다.
    private volatile boolean stopped = false;

    /** 만료 감지 주기 (1분) */
    private static final long CHECK_INTERVAL_MS = 60_000;

    /** 보관 기간 제한 (일) */
    private static final int MAX_STORAGE_DAYS = 3;

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
            checkExpiry();
            try {
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                // 인터럽트 발생 시 스레드를 안전하게 종료한다
                stopped = true;
            }
        }
    }

    /**
     * 보관 기간을 초과한 택배를 만료 상태로 전환한다.
     * 기간 계산과 상태 변경은 Model(LockerRepository)에 위임한다.
     * Model의 synchronized 메서드가 동시 접근을 제어한다.
     */
    private void checkExpiry() {
        lockerRepository.markOverduePackagesAsExpired(MAX_STORAGE_DAYS);
    }

    /**
     * 스레드 종료를 요청한다.
     * 현재 주기가 끝난 후 다음 루프에서 종료된다.
     */
    public void stop() {
        this.stopped = true;
    }
}
