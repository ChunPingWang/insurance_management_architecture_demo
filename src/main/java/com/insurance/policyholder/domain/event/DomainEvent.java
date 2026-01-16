package com.insurance.policyholder.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 領域事件基底類別
 * 所有領域事件都應繼承此類別
 */
public abstract class DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String aggregateId;
    private final String aggregateType;

    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public abstract String getEventType();
}
