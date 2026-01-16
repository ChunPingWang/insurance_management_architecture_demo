package com.insurance.policyholder.domain.model.valueobject;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 身分證字號值物件
 * 格式：首位英文 + 9位數字
 * 包含台灣身分證檢查碼驗證
 */
public final class NationalId {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z][12]\\d{8}$");

    // 台灣身分證字號首位英文字母對應數值
    private static final Map<Character, Integer> LETTER_MAP = Map.ofEntries(
            Map.entry('A', 10), Map.entry('B', 11), Map.entry('C', 12),
            Map.entry('D', 13), Map.entry('E', 14), Map.entry('F', 15),
            Map.entry('G', 16), Map.entry('H', 17), Map.entry('I', 34),
            Map.entry('J', 18), Map.entry('K', 19), Map.entry('L', 20),
            Map.entry('M', 21), Map.entry('N', 22), Map.entry('O', 35),
            Map.entry('P', 23), Map.entry('Q', 24), Map.entry('R', 25),
            Map.entry('S', 26), Map.entry('T', 27), Map.entry('U', 28),
            Map.entry('V', 29), Map.entry('W', 32), Map.entry('X', 30),
            Map.entry('Y', 31), Map.entry('Z', 33)
    );

    private final String value;

    private NationalId(String value) {
        this.value = value;
    }

    /**
     * 從字串建立 NationalId
     */
    public static NationalId of(String value) {
        validateFormat(value);
        validateChecksum(value);
        return new NationalId(value.toUpperCase());
    }

    private static void validateFormat(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("NationalId cannot be null or empty");
        }
        String upperValue = value.toUpperCase();
        if (!PATTERN.matcher(upperValue).matches()) {
            throw new IllegalArgumentException(
                    "Invalid NationalId format. Expected: Letter + 1 or 2 + 8 digits");
        }
    }

    private static void validateChecksum(String value) {
        String upperValue = value.toUpperCase();
        char letter = upperValue.charAt(0);
        Integer letterValue = LETTER_MAP.get(letter);

        if (letterValue == null) {
            throw new IllegalArgumentException("Invalid letter in NationalId: " + letter);
        }

        // 計算檢查碼
        int n1 = letterValue / 10;
        int n2 = letterValue % 10;

        int[] weights = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1};
        int sum = n1 * weights[0] + n2 * weights[1];

        for (int i = 1; i < 10; i++) {
            int digit = Character.getNumericValue(upperValue.charAt(i));
            sum += digit * weights[i + 1];
        }

        if (sum % 10 != 0) {
            throw new IllegalArgumentException("Invalid NationalId checksum");
        }
    }

    public String getValue() {
        return value;
    }

    /**
     * 取得遮罩後的身分證字號
     * 格式：A123***789
     */
    public String getMasked() {
        return value.substring(0, 4) + "***" + value.substring(7);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NationalId that = (NationalId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getMasked();
    }
}
