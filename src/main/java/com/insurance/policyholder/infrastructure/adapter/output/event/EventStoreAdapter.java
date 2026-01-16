package com.insurance.policyholder.infrastructure.adapter.output.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance.policyholder.application.port.output.EventStore;
import com.insurance.policyholder.domain.event.DomainEvent;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.DomainEventJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.repository.DomainEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件儲存庫適配器
 * 實作 Application Layer 的 EventStore Port
 */
@Component
@Transactional
public class EventStoreAdapter implements EventStore {

    private static final Logger log = LoggerFactory.getLogger(EventStoreAdapter.class);

    private final DomainEventJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public EventStoreAdapter(DomainEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void save(DomainEvent event) {
        DomainEventJpaEntity entity = toEntity(event);
        jpaRepository.save(entity);
        log.debug("Saved domain event: {} for aggregate: {}", event.getClass().getSimpleName(), event.getAggregateId());
    }

    @Override
    public void saveAll(List<DomainEvent> events) {
        List<DomainEventJpaEntity> entities = events.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
        log.debug("Saved {} domain events", events.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEvent> findByAggregateId(String aggregateId) {
        return jpaRepository.findByAggregateIdOrderByOccurredOnAsc(aggregateId)
                .stream()
                .map(this::toDomainEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEvent> findByAggregateType(String aggregateType) {
        return jpaRepository.findByAggregateTypeOrderByOccurredOnAsc(aggregateType)
                .stream()
                .map(this::toDomainEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEvent> findByEventType(String eventType) {
        return jpaRepository.findByEventTypeOrderByOccurredOnAsc(eventType)
                .stream()
                .map(this::toDomainEvent)
                .collect(Collectors.toList());
    }

    private DomainEventJpaEntity toEntity(DomainEvent event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize domain event", e);
            throw new RuntimeException("Failed to serialize domain event", e);
        }

        return new DomainEventJpaEntity(
                event.getEventId(),
                event.getAggregateId(),
                event.getAggregateType(),
                event.getClass().getName(),
                payload,
                event.getOccurredOn()
        );
    }

    private DomainEvent toDomainEvent(DomainEventJpaEntity entity) {
        try {
            Class<?> eventClass = Class.forName(entity.getEventType());
            return (DomainEvent) objectMapper.readValue(entity.getPayload(), eventClass);
        } catch (ClassNotFoundException | JsonProcessingException e) {
            log.error("Failed to deserialize domain event: {}", entity.getEventType(), e);
            throw new RuntimeException("Failed to deserialize domain event", e);
        }
    }
}
