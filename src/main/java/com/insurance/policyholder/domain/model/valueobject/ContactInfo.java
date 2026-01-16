package com.insurance.policyholder.domain.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 聯絡資訊值物件
 */
public final class ContactInfo {

    private static final Pattern MOBILE_PHONE_PATTERN = Pattern.compile("^09\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String mobilePhone;
    private final String email;

    private ContactInfo(String mobilePhone, String email) {
        this.mobilePhone = mobilePhone;
        this.email = email;
    }

    /**
     * 建立聯絡資訊
     */
    public static ContactInfo of(String mobilePhone, String email) {
        validateMobilePhone(mobilePhone);
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail != null) {
            validateEmail(normalizedEmail);
        }
        return new ContactInfo(mobilePhone, normalizedEmail);
    }

    private static void validateMobilePhone(String mobilePhone) {
        if (mobilePhone == null || mobilePhone.isBlank()) {
            throw new IllegalArgumentException("MobilePhone cannot be null or empty");
        }
        if (!MOBILE_PHONE_PATTERN.matcher(mobilePhone).matches()) {
            throw new IllegalArgumentException(
                    "Invalid mobile phone format. Expected: 09xxxxxxxx, got: " + mobilePhone);
        }
    }

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }

    private static void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasEmail() {
        return email != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(mobilePhone, that.mobilePhone) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobilePhone, email);
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "mobilePhone='" + mobilePhone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
