package com.crm.validation.lead.infrastructure.adapter.in.commons.mappers;

import java.time.ZoneOffset;

import com.crm.validation.lead.domain.avro.Document;
import com.crm.validation.lead.domain.avro.Email;
import com.crm.validation.lead.domain.avro.LeadId;
import com.crm.validation.lead.domain.avro.LeadPromotedAvroEvent;
import com.crm.validation.lead.domain.avro.LeadRejectedEvent;
import com.crm.validation.lead.domain.avro.LeadState;
import com.crm.validation.lead.domain.avro.PersonalInfo;
import com.crm.validation.lead.domain.avro.PhoneNumber;
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
        LeadId avroId = LeadId.newBuilder()
            .setValue(lead.getId().getValue().toString())
            .build();

        PersonalInfo avroPersonalInfo = 
            PersonalInfo.newBuilder()
                .setName(lead.getPersonalInfo().name())
                .setBirthdate(lead.getPersonalInfo().birthdate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .build();

        Email avroEmail = 
            Email.newBuilder()
                .setValue(lead.getEmail().getValue())
                .build();

        PhoneNumber avroPhoneNumber = 
            PhoneNumber.newBuilder()
                .setValue(lead.getPhoneNumber().getValue())
                .build();

        Document avroDocument = 
            Document.newBuilder()
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
            .setState(LeadState.valueOf(lead.getState().name()))
            .build();
    }

    /**
     * Converts a Lead domain entity to a LeadRejectedEvent.
     * 
     **/
    public static LeadRejectedEvent leadToLeadRejectedEvent(Lead lead) {


        // Create nested Avro objects
        LeadId avroId = LeadId.newBuilder()
            .setValue(lead.getId().getValue().toString())
            .build();

        PersonalInfo avroPersonalInfo = 
            PersonalInfo.newBuilder()
                .setName(lead.getPersonalInfo().name())
                .setBirthdate(lead.getPersonalInfo().birthdate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .build();

        Email avroEmail = 
            Email.newBuilder()
                .setValue(lead.getEmail().getValue())
                .build();

        PhoneNumber avroPhoneNumber = 
            PhoneNumber.newBuilder()
                .setValue(lead.getPhoneNumber().getValue())
                .build();

        Document avroDocument = 
            Document.newBuilder()
                .setType(lead.getDocument().getType())
                .setNumber(lead.getDocument().getNumber()) 
                .build();


        return LeadRejectedEvent.newBuilder()
            .setId(avroId)
            .setPersonalInfo(avroPersonalInfo)
            .setEmail(avroEmail)
            .setPhoneNumber(avroPhoneNumber)
            .setDocument(avroDocument)
            .setState(LeadState.valueOf(lead.getState().name()))
            .build();
    }

}
