package com.crm.validation.lead.domain.model.lead.validator;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;

/**
 * Validation + optional payload produced by a validator.
 * payload is Object so you can store anything (DTO, List, Map…).
 * Most rules will just leave payload == null.
 */
@Builder
public record ValidationOutcome(ValidationResults validation, @Nullable Object payload) {}