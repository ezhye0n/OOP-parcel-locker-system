package model;

/**
 * 택배함 크기를 문자열 대신 타입으로 관리하는 enum.
 * 잘못된 크기 문자열이 섞이는 문제를 막고, 화면 표시용 한글 이름은 getLabel()로 제공한다.
 *
 * Serializable 미구현:
 * Java의 모든 enum은 Enum<E>를 상속하며, Enum<E>가 이미 Serializable을 구현한다.
 * 중복 선언은 불필요하므로 제거한다.
 */
public enum LockerSize {
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

    /** View/Controller가 넘기는 한글 크기 문자열을 enum으로 변환한다. */
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
