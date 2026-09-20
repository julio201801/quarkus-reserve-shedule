package org.mitocode.infrastructure.input.rest.dto;

public record CustomerDto (
    String firstName,
    String lastName,
    String email,
    String phone,
    boolean activeStatus
) {}
