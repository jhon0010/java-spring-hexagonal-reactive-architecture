package com.crm.validation.lead.infrastructure.adapter.in.web.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public class LeadMapper {

    public static Lead dtoToDomain(LeadDto leadDto) {
        return Lead.builder()
                .id(leadDto.id())
                .name(leadDto.name())
                .birthdate(leadDto.birthdate())
                .state(LeadState.CREATED) // Default state is CREATED assume first time
                .email(leadDto.email())
                .phoneNumber(leadDto.phoneNumber())
                .build();
    }

}
