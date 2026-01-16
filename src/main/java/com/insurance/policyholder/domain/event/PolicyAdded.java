package com.insurance.policyholder.domain.event;

import com.insurance.policyholder.domain.model.entity.Policy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保單新增事件
 * 當成功為保戶新增保單時發布此事件
 */
public class PolicyAdded extends DomainEvent {

    private static final String AGGREGATE_TYPE = "PolicyHolder";

    private final String policyId;
    private final String policyType;
    private final BigDecimal premium;
    private final BigDecimal sumInsured;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;

    public PolicyAdded(String policyHolderId, Policy policy) {
        super(policyHolderId, AGGREGATE_TYPE);
        this.policyId = policy.getId().getValue();
        this.policyType = policy.getPolicyType().name();
        this.premium = policy.getPremium().getAmount();
        this.sumInsured = policy.getSumInsured().getAmount();
        this.startDate = policy.getStartDate();
        this.endDate = policy.getEndDate();
        this.status = policy.getStatus().name();
    }

    @Override
    public String getEventType() {
        return "PolicyAdded";
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyType() {
        return policyType;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}
