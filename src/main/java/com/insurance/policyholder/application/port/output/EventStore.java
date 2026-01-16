package com.insurance.policyholder.application.port.output;

import com.insurance.policyholder.domain.event.DomainEvent;

import java.util.List;

/**
 * 事件儲存庫介面
 * 用於持久化領域事件（Event Sourcing 支援）
 *
 * 這是 Output Port，由 Infrastructure Layer 實作
 */
public interface EventStore {

    /**
     * 儲存領域事件
     *
     * @param event 要儲存的領域事件
     */
    void save(DomainEvent event);

    /**
     * 儲存多個領域事件
     *
     * @param events 要儲存的領域事件列表
     */
    void saveAll(List<DomainEvent> events);

    /**
     * 根據聚合根 ID 查詢事件
     *
     * @param aggregateId 聚合根 ID
     * @return 該聚合根的所有事件（按時間排序）
     */
    List<DomainEvent> findByAggregateId(String aggregateId);

    /**
     * 根據聚合根類型查詢事件
     *
     * @param aggregateType 聚合根類型
     * @return 該類型的所有事件（按時間排序）
     */
    List<DomainEvent> findByAggregateType(String aggregateType);

    /**
     * 根據事件類型查詢
     *
     * @param eventType 事件類型
     * @return 該類型的所有事件（按時間排序）
     */
    List<DomainEvent> findByEventType(String eventType);
}
