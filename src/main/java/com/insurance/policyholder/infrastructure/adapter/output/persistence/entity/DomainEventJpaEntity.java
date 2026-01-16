package com.insurance.policyholder.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 領域事件 JPA 實體
 * 用於事件儲存（Event Store）
 */
@Entity
@Table(name = "domain_events", indexes = {
        @Index(name = "idx_aggregate_id", columnList = "aggregateId"),
        @Index(name = "idx_aggregate_type", columnList = "aggregateType"),
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(name = "idx_occurred_on", columnList = "occurredOn")
})
@EntityListeners(AuditingEntityListener.class)
public class DomainEventJpaEntity {

    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;

    @Column(name = "aggregate_id", length = 50, nullable = false)
    private String aggregateId;

    @Column(name = "aggregate_type", length = 50, nullable = false)
    private String aggregateType;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "occurred_on", nullable = false)
    private LocalDateTime occurredOn;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    protected DomainEventJpaEntity() {
    }

    // Builder-style constructor
    public DomainEventJpaEntity(String eventId, String aggregateId, String aggregateType,
                                 String eventType, String payload, LocalDateTime occurredOn) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredOn = occurredOn;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(LocalDateTime occurredOn) {
        this.occurredOn = occurredOn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
