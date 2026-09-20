package org.mitocode.infrastructure.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.mitocode.domain.entities.AvailableSchedule;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static io.quarkus.hibernate.reactive.panache.PanacheEntityBase.count;

@ApplicationScoped
public class AvailableScheduleRepository
        implements PanacheRepositoryBase<AvailableSchedule, UUID> {

    public Uni<Boolean> hasOverlap(
            UUID professionalId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        return count("""
            professional.id = ?1
            and date = ?2
            and startTime < ?4
            and endTime > ?3
            and activeStatus = true
            """,
                professionalId,
                date,
                startTime,
                endTime
        ).map(count -> count > 0);
    }
}