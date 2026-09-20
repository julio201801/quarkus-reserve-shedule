package org.mitocode.aplication;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mitocode.domain.entities.AvailableSchedule;
import org.mitocode.domain.entities.Professional;
import org.mitocode.infrastructure.error.exceptions.BusinessException;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.AvailableScheduleDto;
import org.mitocode.infrastructure.mappers.AvailableScheduleMapper;
import org.mitocode.infrastructure.output.persistence.repository.AvailableScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
@QuarkusTest
class AvailableScheduleServiceUseCaseTest {

    @Test
    void hasOverlap_shouldReturnTrueWhenThereIsOverlap() {
        UUID professionalId = UUID.randomUUID();
        AvailableScheduleRepository repository = new AvailableScheduleRepository() {
            @Override
            public Uni<Boolean> hasOverlap(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
                return Uni.createFrom().item(true);
            }
        };

        AvailableScheduleServiceUseCase service = new AvailableScheduleServiceUseCase(repository, new AvailableScheduleMapper());

        ApiResponse<Boolean> response = service.hasOverlap(professionalId, LocalDate.of(2026, 9, 20), LocalTime.of(9, 0), LocalTime.of(10, 0))
                .await().indefinitely();

        assertNotNull(response);
        assertTrue(response.getData());
    }

    @Test
    void hasOverlap_shouldReturnFalseWhenThereIsNoOverlap() {
        UUID professionalId = UUID.randomUUID();
        AvailableScheduleRepository repository = new AvailableScheduleRepository() {
            @Override
            public Uni<Boolean> hasOverlap(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
                return Uni.createFrom().item(false);
            }
        };

        AvailableScheduleServiceUseCase service = new AvailableScheduleServiceUseCase(repository, new AvailableScheduleMapper());

        ApiResponse<Boolean> response = service.hasOverlap(professionalId, LocalDate.of(2026, 9, 20), LocalTime.of(9, 0), LocalTime.of(10, 0))
                .await().indefinitely();

        assertNotNull(response);
        assertFalse(response.getData());
    }

    @Test
    void save_shouldPersistScheduleWhenNoOverlapExists() {
        UUID professionalId = UUID.randomUUID();
        AvailableScheduleDto dto = new AvailableScheduleDto(
                professionalId.toString(),
                LocalDate.of(2026, 9, 20),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                true
        );

        AvailableScheduleRepository repository = new AvailableScheduleRepository() {
            @Override
            public Uni<Boolean> hasOverlap(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
                return Uni.createFrom().item(false);
            }

            @Override
            public Uni<AvailableSchedule> persist(AvailableSchedule entity) {
                return Uni.createFrom().item(entity);
            }
        };

        AvailableScheduleServiceUseCase service = new AvailableScheduleServiceUseCase(repository, new AvailableScheduleMapper());

        ApiResponse<AvailableScheduleDto> response = service.save(dto).await().indefinitely();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(professionalId.toString(), response.getData().professionalId());
        assertEquals(dto.date(), response.getData().date());
        assertEquals(dto.startTime(), response.getData().startTime());
        assertEquals(dto.endTime(), response.getData().endTime());
        assertEquals(dto.active(), response.getData().active());
    }

    @Test
    void save_shouldThrowBusinessExceptionWhenOverlapExists() {
        UUID professionalId = UUID.randomUUID();
        AvailableScheduleDto dto = new AvailableScheduleDto(
                professionalId.toString(),
                LocalDate.of(2026, 9, 20),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                true
        );

        AvailableScheduleRepository repository = new AvailableScheduleRepository() {
            @Override
            public Uni<Boolean> hasOverlap(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
                return Uni.createFrom().item(true);
            }
        };

        AvailableScheduleServiceUseCase service = new AvailableScheduleServiceUseCase(repository, new AvailableScheduleMapper());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.save(dto).await().indefinitely());

        assertEquals("El horario se solapa con otro registro existente", ex.getMessage());
    }
}
