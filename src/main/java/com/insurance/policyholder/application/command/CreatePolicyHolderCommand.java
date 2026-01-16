package com.insurance.policyholder.application.command;

import java.time.LocalDate;

/**
 * 建立保戶命令
 * CQRS Command - 用於新增保戶的請求
 */
public class CreatePolicyHolderCommand {

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

    public CreatePolicyHolderCommand(
            String nationalId,
            String name,
            String gender,
            LocalDate birthDate,
            String mobilePhone,
            String email,
            String zipCode,
            String city,
            String district,
            String street) {
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
}
