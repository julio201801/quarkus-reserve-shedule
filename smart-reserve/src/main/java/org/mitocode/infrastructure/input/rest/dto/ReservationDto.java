package org.mitocode.infrastructure.input.rest.dto;

import org.mitocode.domain.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        UUID customerId,
        UUID professionalId,
        ReservationStatus status
) {}