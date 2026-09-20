package org.mitocode.infrastructure.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.AvailableSchedule;
import org.mitocode.domain.entities.Professional;
import org.mitocode.infrastructure.input.rest.dto.AvailableScheduleDto;

import java.util.UUID;

@ApplicationScoped
public class AvailableScheduleMapper {
    public AvailableSchedule toEntity(AvailableScheduleDto dto) {
        if (dto == null) return null;

        return AvailableSchedule.builder()
                .professional(Professional.builder().id(UUID.fromString(dto.professionalId())).build())
                .date(dto.date())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .activeStatus(dto.active())
                .build();
    }

    public AvailableScheduleDto toDto(AvailableSchedule schedule) {
        if (schedule == null) return null;

        return new AvailableScheduleDto(
                schedule.getProfessional().getId().toString(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.isActiveStatus()
        );
    }
}
