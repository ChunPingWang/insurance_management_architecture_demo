package com.insurance.policyholder.domain.model.valueobject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * 保單編號值物件
 * 格式：PO + 10位數字
 * 範例：PO0000000001
 */
public final class PolicyId {

    private static final String PREFIX = "PO";
    private static final int SEQUENCE_LENGTH = 10;
    private static final Pattern PATTERN = Pattern.compile("^PO\\d{10}$");
    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis() % 10_000_000_000L);

    private final String value;

    private PolicyId(String value) {
        this.value = value;
    }

    /**
     * 從字串建立 PolicyId
     */
    public static PolicyId of(String value) {
        validate(value);
        return new PolicyId(value);
    }

    /**
     * 產生新的 PolicyId（使用遞增計數器）
     * 用於新增保單時自動產生唯一編號
     */
    public static PolicyId generate() {
        // 使用原子計數器確保唯一性
        long sequence = COUNTER.incrementAndGet() % 10_000_000_000L;
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
        return new PolicyId(PREFIX + formattedSequence);
    }

    /**
     * 產生新的 PolicyId
     * @param sequence 序號
     */
    public static PolicyId generate(long sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("Sequence must be non-negative");
        }
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
        return new PolicyId(PREFIX + formattedSequence);
    }

    private static void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("PolicyId cannot be null or empty");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid PolicyId format. Expected: PO + 10 digits, got: " + value);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyId policyId = (PolicyId) o;
        return Objects.equals(value, policyId.value);
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
