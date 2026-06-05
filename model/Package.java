package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 택배 한 건의 정보와 상태 규칙을 관리하는 Model 클래스.
 *
 * 설계 의도:
 * - 인증코드는 getter로 노출하지 않고 verifyAuthCode()로만 검증한다(정보 은닉).
 * - 상태를 boolean 여러 개가 아닌 PackageStatus enum 하나로 관리해
 *   "만료이면서 동시에 수령 완료" 같은 모순 상태를 차단한다(무결성).
 */
public class Package implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String trackingNumber;    // 송장번호 (고유 식별자)
    private final String recipient;         // 수령인 이름
    private final LocalDateTime storedAt;   // 보관 시각
    private final String authCode;          // 6자리 인증코드. 외부 노출 금지
    private volatile PackageStatus status;  // 현재 상태. 멀티스레드 가시성 위해 volatile

    public Package(String trackingNumber, String recipient, String authCode) {
        this.trackingNumber = requireText(trackingNumber, "trackingNumber");
        this.recipient = requireText(recipient, "recipient");
        this.authCode = requireValidAuthCode(authCode);
        this.storedAt = LocalDateTime.now();
        this.status = PackageStatus.STORED;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public LocalDateTime getStoredAt() {
        return storedAt;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public boolean isStored() {
        return status == PackageStatus.STORED;
    }

    public boolean isExpired() {
        return status == PackageStatus.EXPIRED;
    }

    public boolean isPickedUp() {
        return status == PackageStatus.PICKED_UP;
    }

    /** 인증코드 일치 여부만 반환한다. 코드 자체는 노출하지 않아 정보 은닉을 유지한다. */
    public boolean verifyAuthCode(String inputCode) {
        if (inputCode == null) {
            return false;
        }
        return authCode.equals(inputCode.trim());
    }

    /** 보관 시각 기준으로 최대 보관 일수를 초과했는지 확인한다. */
    public boolean isOverStorageLimit(int maxStorageDays) {
        if (maxStorageDays < 0) {
            throw new IllegalArgumentException("maxStorageDays는 음수일 수 없습니다.");
        }
        long storedDays = ChronoUnit.DAYS.between(storedAt, LocalDateTime.now());
        return storedDays >= maxStorageDays;
    }

    /** 만료 상태로 전환. 단, 이미 수령 완료된 택배는 되돌리지 않는다. */
    public synchronized void markAsExpired() {
        if (status == PackageStatus.PICKED_UP) {
            return;
        }
        this.status = PackageStatus.EXPIRED;
    }

    /** 수령 완료 상태로 전환. 단, 만료된 택배는 수령 처리할 수 없다. */
    public synchronized void markAsPickedUp() {
        if (status == PackageStatus.EXPIRED) {
            throw new IllegalStateException("만료된 택배는 수령 완료 처리할 수 없습니다.");
        }
        this.status = PackageStatus.PICKED_UP;
    }

    public String getStatusText() {
        return status.getLabel();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "는 비어 있을 수 없습니다.");
        }
        return value.trim();
    }

    private static String requireValidAuthCode(String value) {
        String trimmed = requireText(value, "authCode");
        if (!trimmed.matches("\\d{6}")) {
            throw new IllegalArgumentException("authCode는 6자리 숫자여야 합니다.");
        }
        return trimmed;
    }

    @Override
    public String toString() {
        return trackingNumber + " / " + recipient + " / " + storedAt + " / " + getStatusText();
    }
}
