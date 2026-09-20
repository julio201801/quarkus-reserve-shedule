package org.mitocode.infrastructure.input.rest.dto;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailableScheduleDto(
        String professionalId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        boolean active
) {
}