package com.insurance.policyholder.infrastructure.adapter.output.persistence.repository;

import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 保戶 JPA 儲存庫
 * Spring Data JPA 介面
 */
@Repository
public interface PolicyHolderJpaRepository extends JpaRepository<PolicyHolderJpaEntity, String> {

    /**
     * 根據身分證字號查詢
     */
    Optional<PolicyHolderJpaEntity> findByNationalId(String nationalId);

    /**
     * 檢查身分證字號是否已存在
     */
    boolean existsByNationalId(String nationalId);

    /**
     * 根據姓名模糊搜尋
     */
    @Query("SELECT p FROM PolicyHolderJpaEntity p WHERE p.name LIKE %:name%")
    Page<PolicyHolderJpaEntity> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * 根據狀態查詢
     */
    Page<PolicyHolderJpaEntity> findByStatus(PolicyHolderJpaEntity.Status status, Pageable pageable);

    /**
     * 計算符合姓名搜尋的總筆數
     */
    @Query("SELECT COUNT(p) FROM PolicyHolderJpaEntity p WHERE p.name LIKE %:name%")
    long countByNameContaining(@Param("name") String name);

    /**
     * 計算符合狀態的總筆數
     */
    long countByStatus(PolicyHolderJpaEntity.Status status);
}
