package com.insurance.policyholder.infrastructure.adapter.input.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 建立保戶請求 DTO
 */
public class CreatePolicyHolderRequest {

    @NotBlank(message = "National ID is required")
    @Pattern(regexp = "^[A-Z][12]\\d{8}$", message = "Invalid National ID format")
    private String nationalId;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Gender must be MALE or FEMALE")
    private String gender;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message = "Mobile phone is required")
    @Pattern(regexp = "^09\\d{8}$", message = "Invalid mobile phone format. Expected: 09xxxxxxxx")
    private String mobilePhone;

    @Pattern(regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Address is required")
    @Valid
    private AddressRequest address;

    public CreatePolicyHolderRequest() {
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }
}
