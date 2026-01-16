package com.insurance.policyholder.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 地址請求 DTO
 */
public class AddressRequest {

    @NotBlank(message = "Zip code is required")
    @Size(max = 5, message = "Zip code must not exceed 5 characters")
    private String zipCode;

    @NotBlank(message = "City is required")
    @Size(max = 10, message = "City must not exceed 10 characters")
    private String city;

    @NotBlank(message = "District is required")
    @Size(max = 10, message = "District must not exceed 10 characters")
    private String district;

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    public AddressRequest() {
    }

    public AddressRequest(String zipCode, String city, String district, String street) {
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
