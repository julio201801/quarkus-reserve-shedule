package org.mitocode.aplication;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.services.AvailableScheduleService;
import org.mitocode.infrastructure.error.exceptions.BusinessException;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.AvailableScheduleDto;
import org.mitocode.infrastructure.mappers.AvailableScheduleMapper;
import org.mitocode.infrastructure.output.persistence.repository.AvailableScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class AvailableScheduleServiceUseCase implements AvailableScheduleService {
    private final AvailableScheduleRepository availableScheduleRepository;
    private final AvailableScheduleMapper availableScheduleMapper;

    public AvailableScheduleServiceUseCase(AvailableScheduleRepository availableScheduleRepository, AvailableScheduleMapper availableScheduleMapper) {
        this.availableScheduleRepository = availableScheduleRepository;
        this.availableScheduleMapper = availableScheduleMapper;
    }

    @WithSession
    @Override
    public Uni<ApiResponse<Boolean>> hasOverlap(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return availableScheduleRepository.hasOverlap(professionalId, date, startTime, endTime)
                .onItem()
                .transform(found -> {
                    ApiResponse<Boolean> response = new ApiResponse<>();
                    response.setData(found);
                    return response;
                });
    }

    @WithTransaction
    @Override
    public Uni<ApiResponse<AvailableScheduleDto>> save(AvailableScheduleDto schedule) {
        // map to entity
        var entity = availableScheduleMapper.toEntity(schedule);

        // validate overlap
        return availableScheduleRepository.hasOverlap(
                UUID.fromString(schedule.professionalId()),
                schedule.date(),
                schedule.startTime(),
                schedule.endTime()
        ).onItem().transformToUni(hasOverlap -> {
            if (hasOverlap) {
                throw new BusinessException(
                        ErrorType.SCHEDULE_CONFLICT_ERROR,
                        "El horario se solapa con otro registro existente"
                );
            }

            return availableScheduleRepository.persist(entity)
                    .onItem().transform(saved -> {
                        ApiResponse<AvailableScheduleDto> response = new ApiResponse<>();
                        response.setData(availableScheduleMapper.toDto(saved));
                        return response;
                    });
        });
    }
}
