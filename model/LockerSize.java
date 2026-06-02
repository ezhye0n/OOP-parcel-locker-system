package model;

import java.io.Serializable;

/**
 * 택배함 크기를 문자열 대신 명확한 타입으로 관리하기 위한 enum.
 *
 * <p>Controller/View와의 호환을 위해 화면 표시용 한글 이름은 getLabel()로 제공한다.
 * 내부 Model에서는 LockerSize를 사용하므로 잘못된 크기 문자열이 섞이는 문제를 줄일 수 있다.</p>
 */
public enum LockerSize implements Serializable {
    SMALL("소형", "S", "소형 (가로 20cm 이하)"),
    MEDIUM("중형", "M", "중형 (가로 40cm 이하)"),
    LARGE("대형", "L", "대형 (가로 60cm 이하)");

    private final String label;
    private final String idPrefix;
    private final String description;

    LockerSize(String label, String idPrefix, String description) {
        this.label = label;
        this.idPrefix = idPrefix;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public String getDescription() {
        return description;
    }

    /**
     * View/Controller에서 전달되는 한글 크기 문자열을 enum으로 변환한다.
     *
     * @param label 크기 문자열 (소형, 중형, 대형)
     * @return 해당 LockerSize
     */
    public static LockerSize fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("칸 크기는 비어 있을 수 없습니다.");
        }

        String normalized = label.trim();
        for (LockerSize size : values()) {
            if (size.label.equals(normalized)) {
                return size;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 칸 크기입니다: " + label);
    }
}
