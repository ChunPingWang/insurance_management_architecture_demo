package com.insurance.policyholder.application.readmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保單讀取模型
 * CQRS Read Model - 用於查詢回傳的保單資料
 */
public class PolicyReadModel {

    private final String id;
    private final String policyHolderId;
    private final String policyType;
    private final BigDecimal premium;
    private final BigDecimal sumInsured;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;

    public PolicyReadModel(
            String id,
            String policyHolderId,
            String policyType,
            BigDecimal premium,
            BigDecimal sumInsured,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
        this.id = id;
        this.policyHolderId = policyHolderId;
        this.policyType = policyType;
        this.premium = premium;
        this.sumInsured = sumInsured;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
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
