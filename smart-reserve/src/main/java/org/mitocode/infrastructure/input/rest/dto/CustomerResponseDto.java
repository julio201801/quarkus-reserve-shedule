package org.mitocode.infrastructure.input.rest.dto;

public record CustomerResponseDto (
        String id,
        String firstName,
        String lastName,
        String email,
        String phone,
        boolean activeStatus
) {}