package com.crm.validation.lead.domain.events;

import com.crm.validation.lead.domain.model.Lead;
import lombok.Getter;

/**
 * Domain event that represents when a lead has been rejected during validation.
 * This event is emitted whenever a lead transitions to the REJECTED state.
 */
@Getter
public class LeadRejectedEvent extends DomainEvent {
    private final Lead lead;
    private final String reason;

    public LeadRejectedEvent(Lead lead, String reason) {
        super();
        this.lead = lead;
        this.reason = reason;
    }

    public LeadRejectedEvent(Lead lead) {
        this(lead, "Validation failed");
    }
}
