package com.insurance.policyholder.domain.event;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;

/**
 * 保戶更新領域事件
 * 當保戶聯絡資訊或地址被更新時發布
 */
public class PolicyHolderUpdated extends DomainEvent {

    private static final String AGGREGATE_TYPE = "PolicyHolder";

    private final String mobilePhone;
    private final String email;
    private final String zipCode;
    private final String city;
    private final String district;
    private final String street;
    private final Long version;

    public PolicyHolderUpdated(PolicyHolder policyHolder) {
        super(policyHolder.getId().getValue(), AGGREGATE_TYPE);
        this.mobilePhone = policyHolder.getContactInfo().getMobilePhone();
        this.email = policyHolder.getContactInfo().getEmail();
        this.zipCode = policyHolder.getAddress().getZipCode();
        this.city = policyHolder.getAddress().getCity();
        this.district = policyHolder.getAddress().getDistrict();
        this.street = policyHolder.getAddress().getStreet();
        this.version = policyHolder.getVersion();
    }

    @Override
    public String getEventType() {
        return "PolicyHolderUpdated";
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

    public Long getVersion() {
        return version;
    }
}
