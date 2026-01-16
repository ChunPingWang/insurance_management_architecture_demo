package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import java.time.LocalDate;

/**
 * 保戶列表項目回應 DTO
 */
public class PolicyHolderListItemResponse {

    private String id;
    private String maskedNationalId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String mobilePhone;
    private String status;

    public PolicyHolderListItemResponse() {
    }

    public PolicyHolderListItemResponse(
            String id,
            String maskedNationalId,
            String name,
            String gender,
            LocalDate birthDate,
            String mobilePhone,
            String status) {
        this.id = id;
        this.maskedNationalId = maskedNationalId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mobilePhone = mobilePhone;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaskedNationalId() {
        return maskedNationalId;
    }

    public void setMaskedNationalId(String maskedNationalId) {
        this.maskedNationalId = maskedNationalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
