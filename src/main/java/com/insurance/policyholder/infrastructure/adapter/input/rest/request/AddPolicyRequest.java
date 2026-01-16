package com.insurance.policyholder.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增保單請求 DTO
 */
public class AddPolicyRequest {

    @NotBlank(message = "Policy type is required")
    @Pattern(regexp = "^(LIFE|HEALTH|TRAVEL|PROPERTY|AUTO)$", message = "Invalid policy type")
    private String policyType;

    @NotNull(message = "Premium is required")
    @DecimalMin(value = "0", inclusive = false, message = "Premium must be greater than 0")
    private BigDecimal premium;

    @NotNull(message = "Sum insured is required")
    @DecimalMin(value = "0", inclusive = false, message = "Sum insured must be greater than 0")
    private BigDecimal sumInsured;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public AddPolicyRequest() {
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
