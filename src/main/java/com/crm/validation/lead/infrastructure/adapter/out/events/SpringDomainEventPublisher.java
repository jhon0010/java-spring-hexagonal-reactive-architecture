package com.crm.validation.lead.infrastructure.adapter.out.events;

import com.crm.validation.lead.application.ports.out.events.DomainEventPublisher;
import com.crm.validation.lead.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring implementation of the DomainEventPublisher.
 * Uses Spring's ApplicationEventPublisher to broadcast domain events.
 */
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
