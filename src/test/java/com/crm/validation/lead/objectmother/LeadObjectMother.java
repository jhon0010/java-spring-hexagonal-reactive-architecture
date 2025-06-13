package com.crm.validation.lead.objectmother;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public class LeadObjectMother {

    public static LeadDto createValidLeadDto() {
        return LeadDto.builder()
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .build();
    }

}
