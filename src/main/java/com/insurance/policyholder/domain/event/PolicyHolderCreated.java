package com.insurance.policyholder.domain.event;

import java.time.LocalDate;

/**
 * 保戶建立事件
 * 當新保戶成功建立時發布
 */
public class PolicyHolderCreated extends DomainEvent {

    private final String nationalId;
    private final String name;
    private final String gender;
    private final LocalDate birthDate;
    private final String mobilePhone;
    private final String email;
    private final String fullAddress;

    public PolicyHolderCreated(
            String policyHolderId,
            String nationalId,
            String name,
            String gender,
            LocalDate birthDate,
            String mobilePhone,
            String email,
            String fullAddress) {
        super(policyHolderId, "PolicyHolder");
        this.nationalId = nationalId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.fullAddress = fullAddress;
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

    public String getFullAddress() {
        return fullAddress;
    }

    @Override
    public String getEventType() {
        return "PolicyHolderCreated";
    }

    @Override
    public String toString() {
        return "PolicyHolderCreated{" +
                "policyHolderId='" + getAggregateId() + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", name='" + name + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
