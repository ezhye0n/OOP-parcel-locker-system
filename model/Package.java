package model;

import java.time.LocalDateTime;

/**
 * 택배 정보를 저장하는 클래스.
 * 송장번호, 수령인, 보관 시각, 인증코드, 만료 여부를 관리한다.
 * 인증코드는 외부에서 직접 접근할 수 없도록 캡슐화한다.
 */
public class Package {

    private final String trackingNumber;    // 송장번호 (고유 식별자)
    private final String recipient;         // 수령인 이름
    private final LocalDateTime storedAt;   // 보관 시각
    private final String authCode;          // 6자리 인증코드 (외부 노출 금지)
    private boolean isExpired;              // 만료 여부
    private boolean isPickedUp;             // 수령 완료 여부

    /**
     * @param trackingNumber 송장번호
     * @param recipient      수령인 이름
     * @param authCode       발급된 6자리 인증코드
     */
    public Package(String trackingNumber, String recipient, String authCode) {
        this.trackingNumber = trackingNumber;
        this.recipient = recipient;
        this.authCode = authCode;
        this.storedAt = LocalDateTime.now();
        this.isExpired = false;
        this.isPickedUp = false;
    }

    public String getTrackingNumber() { return trackingNumber; }
    public String getRecipient() { return recipient; }
    public LocalDateTime getStoredAt() { return storedAt; }
    public boolean isExpired() { return isExpired; }
    public boolean isPickedUp() { return isPickedUp; }

    /**
     * 입력된 코드가 저장된 인증코드와 일치하는지 확인한다.
     * 인증코드를 직접 반환하지 않고 검증만 수행하여 캡슐화를 유지한다.
     *
     * @param inputCode 사용자가 입력한 코드
     * @return 일치하면 true, 불일치하면 false
     */
    public boolean verifyAuthCode(String inputCode) {
        // TODO: 구현 예정
        return false;
    }

    /**
     * 만료 상태로 전환한다.
     */
    public void markAsExpired() {
        // TODO: 구현 예정
    }

    /**
     * 수령 완료 상태로 전환한다.
     */
    public void markAsPickedUp() {
        // TODO: 구현 예정
    }
}
