package com.insurance.policyholder.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增保單命令
 * CQRS Command - 用於為保戶新增保單的請求
 */
public class AddPolicyCommand {

    private final String policyHolderId;
    private final String policyType;
    private final BigDecimal premium;
    private final BigDecimal sumInsured;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public AddPolicyCommand(
            String policyHolderId,
            String policyType,
            BigDecimal premium,
            BigDecimal sumInsured,
            LocalDate startDate,
            LocalDate endDate) {
        this.policyHolderId = policyHolderId;
        this.policyType = policyType;
        this.premium = premium;
        this.sumInsured = sumInsured;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
