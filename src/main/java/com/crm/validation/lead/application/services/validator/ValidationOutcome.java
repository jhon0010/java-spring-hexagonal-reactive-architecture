package com.crm.validation.lead.application.services.validator;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Validation + optional payload produced by a rule.
 * payload is Object so you can store anything (DTO, List, Map…).
 * Most rules will just leave payload == null.
 */
@Builder
public record ValidationOutcome(ValidationResults validation, @Nullable Object payload) {
}