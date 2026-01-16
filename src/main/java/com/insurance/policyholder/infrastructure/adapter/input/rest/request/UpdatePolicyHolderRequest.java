package com.insurance.policyholder.infrastructure.adapter.input.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 更新保戶請求 DTO
 * 只允許更新聯絡資訊和地址，身分證字號不可修改
 */
public class UpdatePolicyHolderRequest {

    @NotBlank(message = "Mobile phone is required")
    @Pattern(regexp = "^09\\d{8}$", message = "Mobile phone must be a valid Taiwan mobile number (09XXXXXXXX)")
    private String mobilePhone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Address is required")
    @Valid
    private AddressRequest address;

    public UpdatePolicyHolderRequest() {
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
