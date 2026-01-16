package com.insurance.policyholder.application.port.output;

import com.insurance.policyholder.domain.event.DomainEvent;

import java.util.List;

/**
 * 領域事件發布器介面
 * 用於發布領域事件到事件匯流排
 *
 * 這是 Output Port，由 Infrastructure Layer 實作
 */
public interface DomainEventPublisher {

    /**
     * 發布單一領域事件
     *
     * @param event 要發布的領域事件
     */
    void publish(DomainEvent event);

    /**
     * 發布多個領域事件
     *
     * @param events 要發布的領域事件列表
     */
    void publishAll(List<DomainEvent> events);
}
