package com.crm.validation.lead.infrastructure.adapter.in.web;

import com.crm.validation.lead.LeadApplication;
import com.crm.validation.lead.application.ports.out.db.repositories.LeadRepository;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.mappers.LeadWebMapper;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {LeadApplication.class}
)
class LeadControllerEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LeadRepository leadRepository;

    @AfterEach
    void cleanup() {
        leadRepository.deleteAll().block(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("POST /api/leads/validate should promote lead to prospect and save to database")
    void validateEndpointShouldPromoteLeadToProspect() {
        // Given
        LeadDto validLeadDto = LeadObjectMother.createValidLeadDto();

        // When
        webTestClient.post()
                .uri("/api/leads/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(validLeadDto), LeadDto.class)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("PROSPECT");

        Lead lead = LeadWebMapper.INSTANCE.leadDtoToLead(validLeadDto);

        // Verify that the lead was saved to the database with PROSPECT status
        Mono<Lead> savedLeadMono = leadRepository.findByCoreData(
                lead.getEmail(),
                lead.getPhoneNumber(),
                lead.getDocument()
        );

        StepVerifier.create(savedLeadMono)
                .assertNext(savedLead -> {
                    assertNotNull(savedLead);
                    assertEquals(LeadState.PROSPECT, savedLead.getState());
                    assertEquals(validLeadDto.name(), savedLead.getName());
                    assertEquals(validLeadDto.email(), savedLead.getEmail().getValue());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("POST /api/leads/validate should handle idempotent requests")
    void validateEndpointShouldHandleIdempotentRequests() {
        // Given
        LeadDto validLeadDto = LeadObjectMother.createValidLeadDto();

        // First request
        webTestClient.post()
                .uri("/api/leads/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(validLeadDto), LeadDto.class)
                .exchange()
                .expectStatus().isOk();

        // When - Second request with same data
        webTestClient.post()
                .uri("/api/leads/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(validLeadDto), LeadDto.class)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.error.code").value(message -> {
                    assert message.toString().contains("LEAD_ALREADY_EXIST");
                });
    }
}
