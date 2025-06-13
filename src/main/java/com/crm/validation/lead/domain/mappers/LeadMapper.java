package com.crm.validation.lead.domain.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;

public class LeadMapper {

    public static Lead dtoToDomain(LeadDto leadDto) {
        return Lead.builder()
                .documentType(leadDto.documentType())
                .documentNumber(leadDto.documentNumber())
                .name(leadDto.name())
                .birthdate(leadDto.birthdate())
                .state(LeadState.CREATED) // Default state is CREATED assume first time
                .email(leadDto.email())
                .phoneNumber(leadDto.phoneNumber())
                .build();
    }

    public static LeadEntity domainToEntity(Lead lead) {
        return LeadEntity.builder()
                .documentType(lead.getDocumentType())
                .documentNumber(lead.getDocumentNumber())
                .name(lead.getName())
                .birthdate(lead.getBirthdate())
                .state(lead.getState().name())
                .email(lead.getEmail())
                .phoneNumber(lead.getPhoneNumber())
                .build();
    }

    public static Lead entityToDomain(LeadEntity leadEntity) {
        return Lead.builder()
                .id(leadEntity.getId())
                .documentType(leadEntity.getDocumentType())
                .documentNumber(leadEntity.getDocumentNumber())
                .name(leadEntity.getName())
                .birthdate(leadEntity.getBirthdate())
                .state(LeadState.valueOf(leadEntity.getState()))
                .email(leadEntity.getEmail())
                .phoneNumber(leadEntity.getPhoneNumber())
                .build();
    }

    public static Lead changeState(Lead lead, LeadState state) {
        return Lead.builder()
                .id(lead.getId())
                .documentType(lead.getDocumentType())
                .documentNumber(lead.getDocumentNumber())
                .name(lead.getName())
                .birthdate(lead.getBirthdate())
                .state(state)
                .email(lead.getEmail())
                .phoneNumber(lead.getPhoneNumber())
                .build();
    }

}
