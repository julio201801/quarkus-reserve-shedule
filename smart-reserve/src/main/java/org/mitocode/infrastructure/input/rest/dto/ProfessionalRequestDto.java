package org.mitocode.infrastructure.input.rest.dto;

public record ProfessionalRequestDto(
        String firstName,
        String lastName,
        String specialty,
        boolean activeStatus
){}