package com.insurance.policyholder.application.command;

/**
 * 更新保戶命令
 * CQRS Command - 更新保戶聯絡資訊和地址
 * 注意：身分證字號不可修改
 */
public class UpdatePolicyHolderCommand {

    private final String policyHolderId;
    private final String mobilePhone;
    private final String email;
    private final String zipCode;
    private final String city;
    private final String district;
    private final String street;

    public UpdatePolicyHolderCommand(
            String policyHolderId,
            String mobilePhone,
            String email,
            String zipCode,
            String city,
            String district,
            String street) {
        this.policyHolderId = policyHolderId;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
        this.street = street;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
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
}
