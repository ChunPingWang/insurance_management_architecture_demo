package com.insurance.policyholder.domain.model.valueobject;

import java.util.Objects;

/**
 * 地址值物件
 */
public final class Address {

    private static final int MAX_ZIP_CODE_LENGTH = 5;

    private final String zipCode;
    private final String city;
    private final String district;
    private final String street;

    private Address(String zipCode, String city, String district, String street) {
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
        this.street = street;
    }

    /**
     * 建立地址
     */
    public static Address of(String zipCode, String city, String district, String street) {
        validate(zipCode, city, district, street);
        return new Address(zipCode, city, district, street);
    }

    private static void validate(String zipCode, String city, String district, String street) {
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("ZipCode cannot be null or empty");
        }
        if (zipCode.length() > MAX_ZIP_CODE_LENGTH) {
            throw new IllegalArgumentException("ZipCode cannot exceed " + MAX_ZIP_CODE_LENGTH + " characters");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (district == null || district.isBlank()) {
            throw new IllegalArgumentException("District cannot be null or empty");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getStreet() {
        return street;
    }

    /**
     * 取得完整地址字串
     */
    public String getFullAddress() {
        return zipCode + " " + city + district + street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(city, address.city) &&
                Objects.equals(district, address.district) &&
                Objects.equals(street, address.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, city, district, street);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}
