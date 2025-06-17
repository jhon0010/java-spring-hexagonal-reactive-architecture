package com.crm.validation.lead.domain.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = false), componentModel = "spring")
public interface LeadMapper {
    LeadMapper INSTANCE = Mappers.getMapper(LeadMapper.class);

    LeadDto leadToLeadDto(Lead lead);
    Lead leadDtoToLead(LeadDto leadDto);
    LeadEntity leadToLeadEntity(Lead lead);
    Lead leadEntityToLead(LeadEntity leadEntity);

    static Lead changeState(Lead lead, LeadState state) {
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
