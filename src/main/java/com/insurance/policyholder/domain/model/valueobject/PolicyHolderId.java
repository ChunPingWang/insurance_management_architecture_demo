package com.insurance.policyholder.domain.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 保戶編號值物件
 * 格式：PH + 10位數字
 * 範例：PH0000000001
 */
public final class PolicyHolderId {

    private static final String PREFIX = "PH";
    private static final int SEQUENCE_LENGTH = 10;
    private static final Pattern PATTERN = Pattern.compile("^PH\\d{10}$");

    private final String value;

    private PolicyHolderId(String value) {
        this.value = value;
    }

    /**
     * 從字串建立 PolicyHolderId
     */
    public static PolicyHolderId of(String value) {
        validate(value);
        return new PolicyHolderId(value);
    }

    /**
     * 產生新的 PolicyHolderId（使用時間戳記）
     * 用於新增保戶時自動產生唯一編號
     */
    public static PolicyHolderId generate() {
        // 使用系統時間戳記後 10 位數作為序號
        long timestamp = System.currentTimeMillis() % 10_000_000_000L;
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", timestamp);
        return new PolicyHolderId(PREFIX + formattedSequence);
    }

    /**
     * 產生新的 PolicyHolderId
     * @param sequence 序號
     */
    public static PolicyHolderId generate(long sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("Sequence must be non-negative");
        }
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
        return new PolicyHolderId(PREFIX + formattedSequence);
    }

    private static void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("PolicyHolderId cannot be null or empty");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid PolicyHolderId format. Expected: PH + 10 digits, got: " + value);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyHolderId that = (PolicyHolderId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
