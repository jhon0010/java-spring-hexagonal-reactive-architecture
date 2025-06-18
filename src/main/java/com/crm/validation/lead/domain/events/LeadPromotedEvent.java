package com.crm.validation.lead.domain.events;

import com.crm.validation.lead.domain.model.Lead;
import lombok.Getter;

/**
 * Domain event that represents when a lead has been promoted to prospect status.
 * This event is emitted whenever a lead transitions to the PROSPECT state.
 */
@Getter
public class LeadPromotedEvent extends DomainEvent {
    private final Lead lead;

    public LeadPromotedEvent(Lead lead) {
        super();
        this.lead = lead;
    }
}
