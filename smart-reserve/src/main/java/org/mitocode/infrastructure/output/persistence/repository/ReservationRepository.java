package org.mitocode.infrastructure.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Reservation;
import org.mitocode.domain.enums.ReservationStatus;
import org.mitocode.infrastructure.input.rest.dto.ReservationSummaryDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.quarkus.hibernate.reactive.panache.PanacheEntityBase.count;

@ApplicationScoped
public class ReservationRepository implements PanacheRepositoryBase<Reservation, UUID> {

    public Uni<Boolean> hasActiveOverlap(
            UUID professionalId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        return count("""
            professional.id = ?1
            and date = ?2
            and startTime < ?4
            and endTime > ?3
            and status = ?5
            """,
                professionalId,
                date,
                startTime,
                endTime,
                ReservationStatus.CREATED
        ).map(cnt -> cnt > 0);
    }

    public Uni<Reservation> cancelReservation(UUID reservationId) {
        return findById(reservationId)
                .onItem().ifNull().failWith(() -> new RuntimeException("Reservation not found"))
                .onItem().transformToUni(reservation -> {
                    reservation.setStatus(ReservationStatus.CANCELLED);
                    return persist(reservation);
                });
    }
    public Uni<List<Reservation>> findAllReservations() {
        return find("""
            select r
            from Reservation r
            left join fetch r.customer c
            left join fetch r.professional p
            order by r.date asc, r.startTime asc
            """).list();
    }
}
