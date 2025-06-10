package com.crm.validation.lead.infrastructure.adapter.in.web.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public class LeadMapper {

    public static Lead dtoToDomain(LeadDto leadDto) {
        return Lead.builder()
                .id(leadDto.getId())
                .name(leadDto.getName())
                .birthdate(leadDto.getBirthdate())
                .state(LeadState.CREATED)
                .build();
    }

}
