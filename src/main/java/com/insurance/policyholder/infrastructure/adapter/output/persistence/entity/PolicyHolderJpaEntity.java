package com.insurance.policyholder.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 保戶 JPA 實體
 * 用於資料庫持久化
 */
@Entity
@Table(name = "policy_holders", indexes = {
        @Index(name = "idx_national_id", columnList = "nationalId", unique = true),
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class PolicyHolderJpaEntity {

    @Id
    @Column(name = "id", length = 13)
    private String id;

    @Column(name = "national_id", length = 10, nullable = false, unique = true)
    private String nationalId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "gender", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "mobile_phone", length = 10, nullable = false)
    private String mobilePhone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "zip_code", length = 5, nullable = false)
    private String zipCode;

    @Column(name = "city", length = 10, nullable = false)
    private String city;

    @Column(name = "district", length = 10, nullable = false)
    private String district;

    @Column(name = "street", length = 100, nullable = false)
    private String street;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Version
    @Column(name = "version")
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "policyHolder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PolicyJpaEntity> policies = new ArrayList<>();

    // Enums for JPA (避免直接依賴 Domain Layer)
    public enum Gender {
        MALE, FEMALE
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }

    // Default constructor for JPA
    public PolicyHolderJpaEntity() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public List<PolicyJpaEntity> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyJpaEntity> policies) {
        this.policies = policies;
    }

    public void addPolicy(PolicyJpaEntity policy) {
        policies.add(policy);
        policy.setPolicyHolder(this);
    }

    public void removePolicy(PolicyJpaEntity policy) {
        policies.remove(policy);
        policy.setPolicyHolder(null);
    }
}
