package com.insurance.policyholder.application.readmodel;

import java.time.LocalDate;

/**
 * 保戶列表項目讀取模型
 * 用於列表展示的簡化版本
 */
public class PolicyHolderListItemReadModel {

    private final String id;
    private final String maskedNationalId;
    private final String name;
    private final String gender;
    private final LocalDate birthDate;
    private final String mobilePhone;
    private final String status;

    public PolicyHolderListItemReadModel(
            String id,
            String nationalId,
            String name,
            String gender,
            LocalDate birthDate,
            String mobilePhone,
            String status) {
        this.id = id;
        this.maskedNationalId = maskNationalId(nationalId);
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mobilePhone = mobilePhone;
        this.status = status;
    }

    private String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 10) {
            return nationalId;
        }
        return nationalId.substring(0, 4) + "***" + nationalId.substring(7);
    }

    public String getId() {
        return id;
    }

    public String getMaskedNationalId() {
        return maskedNationalId;
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

    public String getStatus() {
        return status;
    }
}
