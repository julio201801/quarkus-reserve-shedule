package org.mitocode.infrastructure.input.rest.dto;

import java.util.UUID;

public record ProfessionalResponseDto (
    String id,
    String firstName,
    String lastName,
    String specialty,
    boolean activeStatus
) {
}
