package org.mitocode.domain.services;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.entities.Reservation;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ReservationDto;
import org.mitocode.infrastructure.input.rest.dto.ReservationSummaryDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReservationService {
    Uni<ApiResponse<ReservationDto>> createReservation(ReservationDto dto);

    Uni<Boolean> hasActiveOverlap(
            UUID professionalId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime);

    Uni<ApiResponse<ReservationDto>> cancelReservation(UUID reservationId);
    Uni<ApiResponse<Map<LocalDate, List<ReservationSummaryDto>>>> getReservationsByDate();
}
