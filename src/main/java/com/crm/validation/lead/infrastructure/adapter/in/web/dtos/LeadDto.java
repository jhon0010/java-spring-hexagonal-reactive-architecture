package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LeadDto(String id, String name, LocalDate birthdate, String email, String phoneNumber,
                      String documentType, int documentNumber) {}
