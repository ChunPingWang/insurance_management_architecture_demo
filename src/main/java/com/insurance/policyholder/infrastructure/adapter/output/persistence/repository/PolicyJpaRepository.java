package com.insurance.policyholder.infrastructure.adapter.output.persistence.repository;

import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 保單 JPA 儲存庫
 * Spring Data JPA 介面
 */
@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyJpaEntity, String> {

    /**
     * 根據保戶 ID 查詢所有保單
     */
    List<PolicyJpaEntity> findByPolicyHolderId(String policyHolderId);

    /**
     * 根據保戶 ID 和保單類型查詢
     */
    List<PolicyJpaEntity> findByPolicyHolderIdAndPolicyType(
            String policyHolderId,
            PolicyJpaEntity.PolicyType policyType);

    /**
     * 根據保戶 ID 和保單狀態查詢
     */
    List<PolicyJpaEntity> findByPolicyHolderIdAndStatus(
            String policyHolderId,
            PolicyJpaEntity.PolicyStatus status);

    /**
     * 根據保戶 ID 查詢（分頁）
     */
    Page<PolicyJpaEntity> findByPolicyHolderId(String policyHolderId, Pageable pageable);

    /**
     * 根據保單類型查詢
     */
    @Query("SELECT p FROM PolicyJpaEntity p WHERE p.policyType = :policyType")
    List<PolicyJpaEntity> findByPolicyType(@Param("policyType") PolicyJpaEntity.PolicyType policyType);

    /**
     * 根據保單狀態查詢
     */
    List<PolicyJpaEntity> findByStatus(PolicyJpaEntity.PolicyStatus status);

    /**
     * 計算保戶的保單數量
     */
    long countByPolicyHolderId(String policyHolderId);
}
