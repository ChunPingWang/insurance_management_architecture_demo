package com.insurance.policyholder.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保單 JPA 實體
 * 用於資料庫持久化
 */
@Entity
@Table(name = "policies", indexes = {
        @Index(name = "idx_policy_holder_id", columnList = "policy_holder_id"),
        @Index(name = "idx_policy_type", columnList = "policyType"),
        @Index(name = "idx_policy_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class PolicyJpaEntity {

    @Id
    @Column(name = "id", length = 12)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_holder_id", nullable = false)
    private PolicyHolderJpaEntity policyHolder;

    @Column(name = "policy_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyType policyType;

    @Column(name = "premium_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal premiumAmount;

    @Column(name = "premium_currency", length = 3, nullable = false)
    private String premiumCurrency;

    @Column(name = "sum_insured", precision = 15, scale = 2, nullable = false)
    private BigDecimal sumInsured;

    @Column(name = "sum_insured_currency", length = 3, nullable = false)
    private String sumInsuredCurrency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    @Version
    @Column(name = "version")
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enums for JPA (與 Domain Layer 保持一致)
    public enum PolicyType {
        LIFE, HEALTH, ACCIDENT, TRAVEL, PROPERTY, AUTO, SAFETY
    }

    public enum PolicyStatus {
        ACTIVE, LAPSED, TERMINATED
    }

    // Default constructor for JPA
    public PolicyJpaEntity() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PolicyHolderJpaEntity getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(PolicyHolderJpaEntity policyHolder) {
        this.policyHolder = policyHolder;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getPremiumCurrency() {
        return premiumCurrency;
    }

    public void setPremiumCurrency(String premiumCurrency) {
        this.premiumCurrency = premiumCurrency;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public String getSumInsuredCurrency() {
        return sumInsuredCurrency;
    }

    public void setSumInsuredCurrency(String sumInsuredCurrency) {
        this.sumInsuredCurrency = sumInsuredCurrency;
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

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
