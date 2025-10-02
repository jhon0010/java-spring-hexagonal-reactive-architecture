package com.crm.validation.lead.infrastructure.adapter.in.commons.mappers;

import java.time.ZoneOffset;

import com.crm.validation.lead.avro.LeadPromotedAvroEvent;
import com.crm.validation.lead.avro.LeadRejectedEvent;
import com.crm.validation.lead.domain.model.Lead;

public class AvroMappers {
    
    /**
     * Converts a Lead domain entity to a LeadPromotedAvroEvent.
     * This is useful for serializing the lead data into an Avro event for Kafka.
     */
    public static LeadPromotedAvroEvent leadToLeadPromotedAvroEvent(Lead lead) {
        if (lead == null) {
            throw new IllegalArgumentException("Lead cannot be null");
        }

        // Create nested Avro objects
        com.crm.validation.lead.avro.LeadId avroId = com.crm.validation.lead.avro.LeadId.newBuilder()
            .setValue(lead.getId().getValue().toString())
            .build();

        com.crm.validation.lead.avro.PersonalInfo avroPersonalInfo = 
            com.crm.validation.lead.avro.PersonalInfo.newBuilder()
                .setName(lead.getPersonalInfo().name())
                .setBirthdate(lead.getPersonalInfo().birthdate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .build();

        com.crm.validation.lead.avro.Email avroEmail = 
            com.crm.validation.lead.avro.Email.newBuilder()
                .setValue(lead.getEmail().getValue())
                .build();

        com.crm.validation.lead.avro.PhoneNumber avroPhoneNumber = 
            com.crm.validation.lead.avro.PhoneNumber.newBuilder()
                .setValue(lead.getPhoneNumber().getValue())
                .build();

        com.crm.validation.lead.avro.Document avroDocument = 
            com.crm.validation.lead.avro.Document.newBuilder()
                .setType(lead.getDocument().getType())
                .setNumber(lead.getDocument().getNumber()) 
                .build();

        // Build the main event
        return LeadPromotedAvroEvent.newBuilder()
            .setId(avroId)
            .setPersonalInfo(avroPersonalInfo)
            .setEmail(avroEmail)
            .setPhoneNumber(avroPhoneNumber)
            .setDocument(avroDocument)
            .setState(com.crm.validation.lead.avro.LeadState.valueOf(lead.getState().name()))
            .build();
    }

    /**
     * Converts a Lead domain entity to a LeadRejectedEvent.
     * 
     **/
    public static LeadRejectedEvent leadToLeadRejectedEvent(Lead lead) {


        // Create nested Avro objects
        com.crm.validation.lead.avro.LeadId avroId = com.crm.validation.lead.avro.LeadId.newBuilder()
            .setValue(lead.getId().getValue().toString())
            .build();

        com.crm.validation.lead.avro.PersonalInfo avroPersonalInfo = 
            com.crm.validation.lead.avro.PersonalInfo.newBuilder()
                .setName(lead.getPersonalInfo().name())
                .setBirthdate(lead.getPersonalInfo().birthdate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .build();

        com.crm.validation.lead.avro.Email avroEmail = 
            com.crm.validation.lead.avro.Email.newBuilder()
                .setValue(lead.getEmail().getValue())
                .build();

        com.crm.validation.lead.avro.PhoneNumber avroPhoneNumber = 
            com.crm.validation.lead.avro.PhoneNumber.newBuilder()
                .setValue(lead.getPhoneNumber().getValue())
                .build();

        com.crm.validation.lead.avro.Document avroDocument = 
            com.crm.validation.lead.avro.Document.newBuilder()
                .setType(lead.getDocument().getType())
                .setNumber(lead.getDocument().getNumber()) 
                .build();


        return LeadRejectedEvent.newBuilder()
            .setId(avroId)
            .setPersonalInfo(avroPersonalInfo)
            .setEmail(avroEmail)
            .setPhoneNumber(avroPhoneNumber)
            .setDocument(avroDocument)
            .setState(com.crm.validation.lead.avro.LeadState.valueOf(lead.getState().name()))
            .build();
    }

}
