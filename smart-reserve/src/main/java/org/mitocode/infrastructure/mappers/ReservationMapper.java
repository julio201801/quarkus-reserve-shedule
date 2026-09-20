package org.mitocode.infrastructure.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Reservation;
import org.mitocode.domain.entities.Customer;
import org.mitocode.domain.entities.Professional;
import org.mitocode.infrastructure.input.rest.dto.ReservationDto;

@ApplicationScoped
public class ReservationMapper {
    public Reservation toEntity(ReservationDto dto) {
        if (dto == null) return null;

        return Reservation.builder()
                .date(dto.date())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .customer(dto.customerId() == null ? null : Customer.builder().id(dto.customerId()).build())
                .professional(dto.professionalId() == null ? null : Professional.builder().id(dto.professionalId()).build())
                .status(dto.status())
                .build();
    }

    public ReservationDto toDto(Reservation reservation) {
        if (reservation == null) return null;

        return new ReservationDto(
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getCustomer() == null ? null : reservation.getCustomer().getId(),
                reservation.getProfessional() == null ? null : reservation.getProfessional().getId(),
                reservation.getStatus()
        );
    }
}
