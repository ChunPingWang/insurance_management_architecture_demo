package com.insurance.policyholder.domain.event;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;

/**
 * 保戶刪除領域事件
 * 當保戶被軟刪除（狀態改為 INACTIVE）時發布
 */
public class PolicyHolderDeleted extends DomainEvent {

    private static final String AGGREGATE_TYPE = "PolicyHolder";

    private final String nationalId;
    private final String name;

    public PolicyHolderDeleted(PolicyHolder policyHolder) {
        super(policyHolder.getId().getValue(), AGGREGATE_TYPE);
        this.nationalId = policyHolder.getNationalId().getMasked();
        this.name = policyHolder.getPersonalInfo().getName();
    }

    @Override
    public String getEventType() {
        return "PolicyHolderDeleted";
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getName() {
        return name;
    }
}
