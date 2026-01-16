package com.insurance.policyholder.application.readmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保戶讀取模型
 * CQRS 模式中的 Query 端使用
 * 扁平化的資料結構，便於查詢與展示
 */
public class PolicyHolderReadModel {

    private final String id;
    private final String nationalId;
    private final String name;
    private final String gender;
    private final LocalDate birthDate;
    private final String mobilePhone;
    private final String email;
    private final String zipCode;
    private final String city;
    private final String district;
    private final String street;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long version;

    public PolicyHolderReadModel(
            String id,
            String nationalId,
            String name,
            String gender,
            LocalDate birthDate,
            String mobilePhone,
            String email,
            String zipCode,
            String city,
            String district,
            String street,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long version) {
        this.id = id;
        this.nationalId = nationalId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
        this.street = street;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getEmail() {
        return email;
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
     * 取得完整地址
     */
    public String getFullAddress() {
        return zipCode + " " + city + district + street;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    /**
     * 取得遮蔽後的身分證字號
     */
    public String getMaskedNationalId() {
        if (nationalId == null || nationalId.length() < 10) {
            return nationalId;
        }
        return nationalId.substring(0, 4) + "***" + nationalId.substring(7);
    }
}
