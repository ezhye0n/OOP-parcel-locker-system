package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 택배 한 건의 정보를 저장하고 상태 규칙을 관리하는 Model 클래스.
 *
 * <p>송장번호, 수령인, 보관 시각, 인증코드, 현재 상태를 가진다.
 * 인증코드는 외부에 직접 반환하지 않고 verifyAuthCode 메서드를 통해서만 검증한다.</p>
 *
 * <p>상태는 boolean 여러 개가 아니라 PackageStatus enum 하나로 관리한다. 따라서
 * "만료이면서 동시에 수령 완료" 같은 모순된 상태가 만들어지지 않는다.</p>
 */
public class Package implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String trackingNumber;    // 송장번호 (고유 식별자)
    private final String recipient;         // 수령인 이름
    private final LocalDateTime storedAt;   // 보관 시각
    private final String authCode;          // 6자리 인증코드. 외부 노출 금지
    private PackageStatus status;           // 현재 택배 상태

    /**
     * @param trackingNumber 송장번호
     * @param recipient      수령인 이름
     * @param authCode       발급된 6자리 인증코드
     */
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

    /**
     * 입력된 코드가 저장된 인증코드와 일치하는지 검증한다.
     * 인증코드 자체를 getter로 제공하지 않아 정보 은닉을 유지한다.
     *
     * @param inputCode 사용자가 입력한 코드
     * @return 인증코드가 일치하면 true, 아니면 false
     */
    public boolean verifyAuthCode(String inputCode) {
        if (inputCode == null) {
            return false;
        }
        return authCode.equals(inputCode.trim());
    }

    /**
     * 보관 시각 기준으로 최대 보관 일수를 초과했는지 확인한다.
     *
     * @param maxStorageDays 최대 보관 일수
     * @return 초과했으면 true
     */
    public boolean isOverStorageLimit(int maxStorageDays) {
        if (maxStorageDays < 0) {
            throw new IllegalArgumentException("maxStorageDays는 음수일 수 없습니다.");
        }
        long storedDays = ChronoUnit.DAYS.between(storedAt, LocalDateTime.now());
        return storedDays > maxStorageDays;
    }

    /**
     * 택배를 만료 상태로 전환한다.
     * 이미 수령 완료된 택배는 만료 상태로 되돌리지 않는다.
     */
    public void markAsExpired() {
        if (status == PackageStatus.PICKED_UP) {
            return;
        }
        this.status = PackageStatus.EXPIRED;
    }

    /**
     * 택배를 수령 완료 상태로 전환한다.
     * 만료된 택배는 일반 수령 완료로 바꿀 수 없도록 보호한다.
     */
    public void markAsPickedUp() {
        if (status == PackageStatus.EXPIRED) {
            throw new IllegalStateException("만료된 택배는 수령 완료 처리할 수 없습니다.");
        }
        this.status = PackageStatus.PICKED_UP;
    }

    /**
     * 화면이나 로그에 표시할 상태 문자열을 반환한다.
     *
     * @return 만료, 수령 완료, 사용 중 중 하나
     */
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
