package com.insurance.policyholder.infrastructure.adapter.output.event;

import com.insurance.policyholder.application.port.output.DomainEventPublisher;
import com.insurance.policyholder.application.port.output.EventStore;
import com.insurance.policyholder.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 領域事件發布器適配器
 * 實作 Application Layer 的 DomainEventPublisher Port
 * 使用 Spring ApplicationEventPublisher 發布事件
 */
@Component
public class DomainEventPublisherAdapter implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisherAdapter.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventStore eventStore;

    public DomainEventPublisherAdapter(
            ApplicationEventPublisher applicationEventPublisher,
            EventStore eventStore) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.eventStore = eventStore;
    }

    @Override
    public void publish(DomainEvent event) {
        // 先儲存到 Event Store
        eventStore.save(event);

        // 再發布到 Spring Event Bus
        applicationEventPublisher.publishEvent(event);
        log.info("Published domain event: {} for aggregate: {}",
                event.getClass().getSimpleName(),
                event.getAggregateId());
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        // 批次儲存到 Event Store
        eventStore.saveAll(events);

        // 逐一發布到 Spring Event Bus
        events.forEach(event -> {
            applicationEventPublisher.publishEvent(event);
            log.info("Published domain event: {} for aggregate: {}",
                    event.getClass().getSimpleName(),
                    event.getAggregateId());
        });

        log.info("Published {} domain events", events.size());
    }
}
