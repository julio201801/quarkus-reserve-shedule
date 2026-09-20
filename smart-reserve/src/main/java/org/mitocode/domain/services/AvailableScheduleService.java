package org.mitocode.domain.services;

import io.smallrye.mutiny.Uni;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.AvailableScheduleDto;
import org.mitocode.infrastructure.input.rest.dto.CustomerDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface AvailableScheduleService {
    Uni<ApiResponse<Boolean>> hasOverlap(
            UUID professionalId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime);

    Uni<ApiResponse<AvailableScheduleDto>> save(
            AvailableScheduleDto schedule);
}
