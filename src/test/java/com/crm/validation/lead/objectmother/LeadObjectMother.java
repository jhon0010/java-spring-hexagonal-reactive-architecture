package com.crm.validation.lead.objectmother;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public class LeadObjectMother {

    public static LeadDto createValidLeadDto() {
        return LeadDto.builder()
                .id("123e4567-e89b-12d3-a456-426614174000")
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .build();
    }

}
