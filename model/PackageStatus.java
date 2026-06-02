package model;

import java.io.Serializable;

/**
 * 택배의 생명주기 상태를 명확하게 표현하는 enum.
 *
 * <p>기존의 expired/pickedUp boolean 조합은 동시에 true가 되는 등 잘못된 상태를 만들 수 있다.
 * enum을 사용하면 한 택배가 한 번에 하나의 상태만 가지므로 데이터 무결성을 높일 수 있다.</p>
 */
public enum PackageStatus implements Serializable {
    STORED("사용 중"),
    EXPIRED("만료"),
    PICKED_UP("수령 완료");

    private final String label;

    PackageStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
