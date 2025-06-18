package com.crm.validation.lead.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events in the system.
 * Provides common properties like ID and timestamp.
 */
@Getter
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
    }
}
