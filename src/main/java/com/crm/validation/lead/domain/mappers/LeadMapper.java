package com.crm.validation.lead.domain.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public class LeadMapper {

    public static Lead dtoToDomain(LeadDto leadDto) {
        return Lead.builder()
                .id(leadDto.id())
                .documentType(leadDto.documentType())
                .documentNumber(leadDto.documentNumber())
                .name(leadDto.name())
                .birthdate(leadDto.birthdate())
                .state(LeadState.CREATED) // Default state is CREATED assume first time
                .email(leadDto.email())
                .phoneNumber(leadDto.phoneNumber())
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
