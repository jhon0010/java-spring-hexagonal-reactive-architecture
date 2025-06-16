package com.crm.validation.lead.infrastructure.adapter.in.web;

import com.crm.validation.lead.LeadApplication;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.cli.LeadCrmValidatorCli;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;
import com.crm.validation.lead.infrastructure.adapter.out.db.repositories.LeadRepository;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {LeadApplication.class, LeadControllerEndToEndTest.TestConfig.class}
)
class LeadControllerEndToEndTest {

    // Create a configuration to replace the real CommandLineRunner
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public LeadCrmValidatorCli testCliRunner() {
            return new LeadCrmValidatorCli(null) {
                @Override
                public void run(String... args) {
                    // Do nothing - override run method to prevent CLI execution
                }
            };
        }
    }

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
                .jsonPath("$.lead.state").isEqualTo("PROSPECT");

        // Verify that the lead was saved to the database with PROSPECT status
        Mono<LeadEntity> savedLeadMono = leadRepository.findByCoreData(
                validLeadDto.email(),
                validLeadDto.phoneNumber(),
                validLeadDto.documentNumber()
        );

        StepVerifier.create(savedLeadMono)
                .assertNext(leadEntity -> {
                    assertNotNull(leadEntity);
                    assertEquals(LeadState.PROSPECT.name(), leadEntity.getState());
                    assertEquals(validLeadDto.name(), leadEntity.getName());
                    assertEquals(validLeadDto.email(), leadEntity.getEmail());
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
