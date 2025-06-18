package com.crm.validation.lead.infrastructure.adapter.in.events;

import com.crm.validation.lead.domain.events.LeadPromotedEvent;
import com.crm.validation.lead.domain.events.LeadRejectedEvent;
import com.crm.validation.lead.domain.model.Lead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for domain events related to leads.
 * This class demonstrates how side effects can be handled in a decoupled way
 * by responding to domain events rather than being directly coupled to business logic.
 */
@Slf4j
@Component
public class LeadEventListener {

    /**
     * Handles the LeadPromotedEvent by performing side effects like notifications,
     * audit logging, or integration with other systems.
     *
     * @param event The LeadPromotedEvent containing the promoted lead
     */
    @EventListener
    public void handleLeadPromoted(LeadPromotedEvent event) {
        Lead promotedLead = event.getLead();
        log.info("EVENT: Lead with ID {} was promoted to PROSPECT status",
                promotedLead.getId().getValue());

        // Example side effects that could be implemented:
        // 1. Send notification to sales team
        // notificationService.notifySalesTeam(promotedLead);

        // 2. Record analytics
        // analyticsService.recordLeadPromotion(promotedLead);

        // 3. Schedule follow-up tasks
        // taskScheduler.scheduleFollowUp(promotedLead);
    }

    /**
     * Handles the LeadRejectedEvent by performing side effects like
     * notifications, logging, or reporting.
     *
     * @param event The LeadRejectedEvent containing the rejected lead
     */
    @EventListener
    public void handleLeadRejected(LeadRejectedEvent event) {
        Lead rejectedLead = event.getLead();
        log.info("EVENT: Lead with ID {} was REJECTED during validation",
                rejectedLead.getId().getValue());

        // Example side effects that could be implemented:
        // 1. Send notification to review team
        // notificationService.notifyReviewTeam(rejectedLead, event.getReason());

        // 2. Record rejection metrics
        // analyticsService.recordLeadRejection(rejectedLead, event.getReason());
    }
}
