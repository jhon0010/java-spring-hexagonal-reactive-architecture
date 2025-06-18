package com.crm.validation.lead.application.ports.out.events;

import com.crm.validation.lead.domain.events.DomainEvent;

/**
 * Port for publishing domain events from the application layer.
 * This interface decouples the domain layer from the event publishing mechanism.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event to all registered listeners.
     *
     * @param event The domain event to publish
     */
    void publish(DomainEvent event);
}
