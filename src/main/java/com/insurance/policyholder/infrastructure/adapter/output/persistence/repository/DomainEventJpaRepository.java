package com.insurance.policyholder.infrastructure.adapter.output.persistence.repository;

import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.DomainEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 領域事件 JPA 儲存庫
 * Spring Data JPA 介面
 */
@Repository
public interface DomainEventJpaRepository extends JpaRepository<DomainEventJpaEntity, String> {

    /**
     * 根據聚合根 ID 查詢事件（按時間排序）
     */
    @Query("SELECT e FROM DomainEventJpaEntity e WHERE e.aggregateId = :aggregateId ORDER BY e.occurredOn ASC")
    List<DomainEventJpaEntity> findByAggregateIdOrderByOccurredOnAsc(@Param("aggregateId") String aggregateId);

    /**
     * 根據聚合根類型查詢事件（按時間排序）
     */
    @Query("SELECT e FROM DomainEventJpaEntity e WHERE e.aggregateType = :aggregateType ORDER BY e.occurredOn ASC")
    List<DomainEventJpaEntity> findByAggregateTypeOrderByOccurredOnAsc(@Param("aggregateType") String aggregateType);

    /**
     * 根據事件類型查詢（按時間排序）
     */
    @Query("SELECT e FROM DomainEventJpaEntity e WHERE e.eventType = :eventType ORDER BY e.occurredOn ASC")
    List<DomainEventJpaEntity> findByEventTypeOrderByOccurredOnAsc(@Param("eventType") String eventType);

    /**
     * 根據時間範圍查詢事件
     */
    @Query("SELECT e FROM DomainEventJpaEntity e WHERE e.occurredOn BETWEEN :start AND :end ORDER BY e.occurredOn ASC")
    List<DomainEventJpaEntity> findByOccurredOnBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 根據聚合根 ID 和事件類型查詢
     */
    @Query("SELECT e FROM DomainEventJpaEntity e WHERE e.aggregateId = :aggregateId AND e.eventType = :eventType ORDER BY e.occurredOn ASC")
    List<DomainEventJpaEntity> findByAggregateIdAndEventType(
            @Param("aggregateId") String aggregateId,
            @Param("eventType") String eventType);
}
