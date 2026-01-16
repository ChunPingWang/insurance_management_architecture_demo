package com.insurance.policyholder.domain.model.entity;

import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.Money;
import com.insurance.policyholder.domain.model.valueobject.PolicyId;

import java.time.LocalDate;

/**
 * 保單實體
 * Domain Layer - 純 Java，不依賴任何框架
 *
 * 屬於 PolicyHolder 聚合根內的實體
 */
public class Policy {

    private final PolicyId id;
    private final PolicyType policyType;
    private final Money premium;
    private final Money sumInsured;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private PolicyStatus status;
    private Long version;

    /**
     * 私有建構子 - 只能透過工廠方法建立
     */
    private Policy(
            PolicyId id,
            PolicyType policyType,
            Money premium,
            Money sumInsured,
            LocalDate startDate,
            LocalDate endDate,
            PolicyStatus status,
            Long version) {
        this.id = id;
        this.policyType = policyType;
        this.premium = premium;
        this.sumInsured = sumInsured;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.version = version;
    }

    /**
     * 建立新保單（工廠方法）
     */
    public static Policy create(
            PolicyType policyType,
            Money premium,
            Money sumInsured,
            LocalDate startDate,
            LocalDate endDate) {

        validateDates(startDate, endDate);

        PolicyId id = PolicyId.generate();
        return new Policy(
                id,
                policyType,
                premium,
                sumInsured,
                startDate,
                endDate,
                PolicyStatus.ACTIVE,
                0L
        );
    }

    /**
     * 從持久化重建保單（不產生事件）
     * 用於從資料庫讀取時的重建
     */
    public static Policy reconstitute(
            PolicyId id,
            PolicyType policyType,
            Money premium,
            Money sumInsured,
            LocalDate startDate,
            LocalDate endDate,
            PolicyStatus status,
            Long version) {

        return new Policy(
                id,
                policyType,
                premium,
                sumInsured,
                startDate,
                endDate,
                status,
                version
        );
    }

    /**
     * 驗證日期
     */
    private static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    /**
     * 終止保單
     */
    public void terminate() {
        if (status != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only active policies can be terminated");
        }
        this.status = PolicyStatus.TERMINATED;
    }

    /**
     * 檢查保單是否有效
     */
    public boolean isActive() {
        return status == PolicyStatus.ACTIVE;
    }

    /**
     * 檢查保單是否在有效期內
     */
    public boolean isWithinValidPeriod(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // Getters
    public PolicyId getId() {
        return id;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public Money getPremium() {
        return premium;
    }

    public Money getSumInsured() {
        return sumInsured;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }
}
