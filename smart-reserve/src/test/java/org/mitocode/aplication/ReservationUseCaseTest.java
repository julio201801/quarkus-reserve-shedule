package org.mitocode.aplication;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mitocode.domain.entities.Customer;
import org.mitocode.domain.entities.Professional;
import org.mitocode.domain.entities.Reservation;
import org.mitocode.domain.enums.ReservationStatus;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ReservationSummaryDto;
import org.mitocode.infrastructure.output.persistence.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@QuarkusTest
class ReservationUseCaseTest {

    @Test
    void getReservationsByDate_shouldGroupOnlyActiveReservationsByDate() {
        ReservationRepository reservationRepository = new ReservationRepository() {
            @Override
            public Uni<List<Reservation>> findAllReservations() {
                Customer customer1 = createCustomer(UUID.randomUUID(), "Ana", "López");
                Customer customer2 = createCustomer(UUID.randomUUID(), "Luis", "Pérez");
                Professional professional = createProfessional(UUID.randomUUID(), "Carlos", "Mendoza");

                Reservation activeReservation1 = createReservation(
                        LocalDate.of(2026, 9, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        customer1,
                        professional,
                        ReservationStatus.CREATED
                );

                Reservation activeReservation2 = createReservation(
                        LocalDate.of(2026, 9, 21),
                        LocalTime.of(11, 0),
                        LocalTime.of(12, 0),
                        customer2,
                        professional,
                        ReservationStatus.CREATED
                );

                Reservation cancelledReservation = createReservation(
                        LocalDate.of(2026, 9, 22),
                        LocalTime.of(15, 0),
                        LocalTime.of(16, 0),
                        customer1,
                        professional,
                        ReservationStatus.CANCELLED
                );

                return Uni.createFrom().item(List.of(activeReservation1, activeReservation2, cancelledReservation));
            }
        };

        ReservationUseCase useCase = new ReservationUseCase(
                reservationRepository,
                null,
                null,
                null,
                null
        );

        ApiResponse<Map<LocalDate, List<ReservationSummaryDto>>> response =
                useCase.getReservationsByDate().await().indefinitely();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(1, response.getData().get(LocalDate.of(2026, 9, 20)).size());
        assertEquals(1, response.getData().get(LocalDate.of(2026, 9, 21)).size());

        ReservationSummaryDto first = response.getData().get(LocalDate.of(2026, 9, 20)).get(0);
        assertEquals("Ana López", first.customerName());
        assertEquals("Carlos Mendoza", first.professionalName());
    }

    private Customer createCustomer(UUID id, String firstName, String lastName) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(firstName.toLowerCase() + "@mail.com");
        customer.setPhone("123456789");
        customer.setActiveStatus(true);
        return customer;
    }

    private Professional createProfessional(UUID id, String firstName, String lastName) {
        Professional professional = new Professional();
        professional.setId(id);
        professional.setFirstName(firstName);
        professional.setLastName(lastName);
        professional.setSpecialty("Cardiología");
        professional.setActiveStatus(true);
        return professional;
    }

    private Reservation createReservation(LocalDate date,
                                         LocalTime startTime,
                                         LocalTime endTime,
                                         Customer customer,
                                         Professional professional,
                                         ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setCustomer(customer);
        reservation.setProfessional(professional);
        reservation.setStatus(status);
        return reservation;
    }
}
