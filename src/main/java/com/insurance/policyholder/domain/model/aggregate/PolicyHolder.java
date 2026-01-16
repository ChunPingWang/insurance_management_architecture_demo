package com.insurance.policyholder.domain.model.aggregate;

import com.insurance.policyholder.domain.event.DomainEvent;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 保戶聚合根
 * Domain Layer - 純 Java，不依賴任何框架
 *
 * 負責管理保戶及其下的保單（Policy Entity）
 */
public class PolicyHolder {

    private final PolicyHolderId id;
    private final NationalId nationalId;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private Address address;
    private PolicyHolderStatus status;
    private final List<Policy> policies;
    private Long version;

    // 領域事件集合（用於發布事件）
    private final List<DomainEvent> domainEvents;

    /**
     * 私有建構子 - 只能透過工廠方法建立
     */
    private PolicyHolder(
            PolicyHolderId id,
            NationalId nationalId,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            Address address,
            PolicyHolderStatus status,
            Long version) {
        this.id = id;
        this.nationalId = nationalId;
        this.personalInfo = personalInfo;
        this.contactInfo = contactInfo;
        this.address = address;
        this.status = status;
        this.version = version;
        this.policies = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * 建立新保戶（工廠方法）
     * 會產生 PolicyHolderCreated 領域事件
     */
    public static PolicyHolder create(
            NationalId nationalId,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            Address address) {

        PolicyHolderId id = PolicyHolderId.generate();
        return create(id, nationalId, personalInfo, contactInfo, address);
    }

    /**
     * 建立新保戶（工廠方法 - 指定 ID）
     * 主要用於測試或資料遷移
     */
    public static PolicyHolder create(
            PolicyHolderId id,
            NationalId nationalId,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            Address address) {

        PolicyHolder policyHolder = new PolicyHolder(
                id,
                nationalId,
                personalInfo,
                contactInfo,
                address,
                PolicyHolderStatus.ACTIVE,
                0L
        );

        // 發布領域事件 - 將在 Phase 3 T053 中實作 PolicyHolderCreated 事件
        // policyHolder.registerEvent(new PolicyHolderCreated(policyHolder));

        return policyHolder;
    }

    /**
     * 從持久化重建保戶（不產生事件）
     * 用於從資料庫讀取時的重建
     */
    public static PolicyHolder reconstitute(
            PolicyHolderId id,
            NationalId nationalId,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            Address address,
            PolicyHolderStatus status,
            Long version) {

        return new PolicyHolder(
                id,
                nationalId,
                personalInfo,
                contactInfo,
                address,
                status,
                version
        );
    }

    /**
     * 新增保單
     * 只有 ACTIVE 狀態的保戶可以新增保單
     */
    public void addPolicy(Policy policy) {
        if (status != PolicyHolderStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add policy to inactive policyholder");
        }
        policies.add(policy);
        // 發布領域事件 - 將在 Phase 7 T101 中實作 PolicyAdded 事件
    }

    /**
     * 新增重建的保單（不產生事件）
     * 用於從資料庫讀取時的重建
     */
    public void addReconstitutedPolicy(Policy policy) {
        policies.add(policy);
    }

    /**
     * 更新聯絡資訊
     */
    public void updateContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
        // 發布領域事件 - 將在 Phase 5 T081 中實作 PolicyHolderUpdated 事件
    }

    /**
     * 更新地址
     */
    public void updateAddress(Address address) {
        this.address = address;
        // 發布領域事件 - 將在 Phase 5 T081 中實作 PolicyHolderUpdated 事件
    }

    /**
     * 停用保戶（軟刪除）
     */
    public void deactivate() {
        if (status == PolicyHolderStatus.INACTIVE) {
            throw new IllegalStateException("PolicyHolder is already inactive");
        }
        this.status = PolicyHolderStatus.INACTIVE;
        // 發布領域事件 - 將在 Phase 6 T090 中實作 PolicyHolderDeleted 事件
    }

    /**
     * 註冊領域事件
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 取得並清除所有領域事件
     */
    public List<DomainEvent> getDomainEventsAndClear() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // Getters
    public PolicyHolderId getId() {
        return id;
    }

    public NationalId getNationalId() {
        return nationalId;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Address getAddress() {
        return address;
    }

    public PolicyHolderStatus getStatus() {
        return status;
    }

    public List<Policy> getPolicies() {
        return Collections.unmodifiableList(policies);
    }

    public Long getVersion() {
        return version;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
