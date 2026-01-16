package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

/**
 * 地址回應 DTO
 */
public class AddressResponse {

    private String zipCode;
    private String city;
    private String district;
    private String street;
    private String fullAddress;

    public AddressResponse() {
    }

    public AddressResponse(String zipCode, String city, String district, String street) {
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
        this.street = street;
        this.fullAddress = zipCode + " " + city + district + street;
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

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
