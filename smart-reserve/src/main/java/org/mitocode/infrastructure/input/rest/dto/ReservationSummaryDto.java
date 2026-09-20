package org.mitocode.infrastructure.input.rest.dto;

import org.mitocode.domain.enums.ReservationStatus;

import java.time.LocalTime;
import java.util.UUID;

public record ReservationSummaryDto(
        UUID reservationId,
        LocalTime startTime,
        LocalTime endTime,
        String customerName,
        String professionalName,
        ReservationStatus status
) {
}
