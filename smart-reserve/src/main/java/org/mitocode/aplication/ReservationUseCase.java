package org.mitocode.aplication;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.mitocode.domain.entities.AvailableSchedule;
import org.mitocode.domain.entities.Customer;
import org.mitocode.domain.entities.Professional;
import org.mitocode.domain.entities.Reservation;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.enums.ReservationStatus;
import org.mitocode.domain.services.ReservationService;
import org.mitocode.infrastructure.error.exceptions.BusinessException;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ReservationDto;
import org.mitocode.infrastructure.input.rest.dto.ReservationSummaryDto;
import org.mitocode.infrastructure.mappers.ReservationMapper;
import org.mitocode.infrastructure.output.persistence.repository.AvailableScheduleRepository;
import org.mitocode.infrastructure.output.persistence.repository.CustomerRepository;
import org.mitocode.infrastructure.output.persistence.repository.ProfessionalRepository;
import org.mitocode.infrastructure.output.persistence.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ReservationUseCase implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final AvailableScheduleRepository availableScheduleRepository;
    private final CustomerRepository customerRepository;
    private final ProfessionalRepository professionalRepository;
    private final ReservationMapper reservationMapper;

    public ReservationUseCase(ReservationRepository reservationRepository,
                              AvailableScheduleRepository availableScheduleRepository,
                              CustomerRepository customerRepository,
                              ProfessionalRepository professionalRepository,
                              ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.availableScheduleRepository = availableScheduleRepository;
        this.customerRepository = customerRepository;
        this.professionalRepository = professionalRepository;
        this.reservationMapper = reservationMapper;
    }

    @WithSession
    @Override
    public Uni<Boolean> hasActiveOverlap(UUID professionalId, java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        return reservationRepository.hasActiveOverlap(professionalId, date, startTime, endTime);
    }

    /**
     * Cancela la reserva y libera disponibilidad (crea un AvailableSchedule si no existe uno que cubra el intervalo).
     */

    @WithTransaction
    @Override
    public Uni<ApiResponse<ReservationDto>> cancelReservation(UUID reservationId) {
        return reservationRepository.cancelReservation(reservationId)
                .onItem().transformToUni(reservation -> {
                    UUID professionalId = reservation.getProfessional().getId();
                    java.time.LocalDate date = reservation.getDate();
                    java.time.LocalTime start = reservation.getStartTime();
                    java.time.LocalTime end = reservation.getEndTime();

                    return availableScheduleRepository.hasOverlap(professionalId, date, start, end)
                            .onItem().transformToUni(exists -> {
                                if (exists) {
                                    return Uni.createFrom().item(reservation);
                                }
                                AvailableSchedule avail = AvailableSchedule.create(
                                        reservation.getProfessional(),
                                        reservation.getDate(),
                                        reservation.getStartTime(),
                                        reservation.getEndTime(),
                                        true
                                );
                                return availableScheduleRepository.persist(avail)
                                        .map(a -> reservation);
                            });
                })
                .onItem().transform(reservation -> {
                    ApiResponse<ReservationDto> response = new ApiResponse<>();
                    response.setData(reservationMapper.toDto(reservation));
                    return response;
                });
    }

    /**
     * Crea una reserva aplicando las reglas:
     * - Debe existir un horario disponible que cubra el intervalo solicitado.
     * - El profesional no puede tener solapamientos con otras reservas activas.
     * - Cliente y profesional deben estar activos.
     */

    @Timeout(value = 2)
    @Retry(
            maxRetries = 2,
            delay = 500
    )
    @CircuitBreaker(
            requestVolumeThreshold = 5,
            failureRatio = 0.5,
            delay = 5000
    )
    @Fallback(fallbackMethod = "fallbackCreateReservation")
    public Uni<ApiResponse<ReservationDto>> fallbackCreateReservation(ReservationDto dto) {
        ApiResponse<ReservationDto> response = new ApiResponse<>();
        response.setStatusCode(503);
        response.setData(null);
        return Uni.createFrom().item(response);
    }
    @WithTransaction
    public Uni<ApiResponse<ReservationDto>> createReservation(ReservationDto dto) {
        System.out.println("Paso 1: " + dto);
        if (dto == null) {
            throw new BusinessException(ErrorType.VALIDATION_ERROR, ErrorType.VALIDATION_ERROR.getDescription());
        }
        System.out.println("Paso 2: " + dto);
        return customerRepository.findById(dto.customerId())
                .onItem().ifNull().failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_CUSTOMER,
                        ErrorType.BUSINESS_NOT_FOUND_CUSTOMER.getDescription()
                ))
                .onItem().transformToUni(customer -> {
                    if (!customer.isActiveStatus()) {
                        throw new BusinessException(ErrorType.BUSINESS_NOT_FOUND_CUSTOMER_NOT_ACTIVE,
                                ErrorType.BUSINESS_NOT_FOUND_CUSTOMER_NOT_ACTIVE.getDescription());
                    }
                    System.out.println("Paso 3: " + dto);
                    return professionalRepository.findById(dto.professionalId())
                            .onItem().ifNull().failWith(() -> new BusinessException(
                                    ErrorType.BUSINESS_NOT_FOUND_PROFESSIONAL,
                                    ErrorType.BUSINESS_NOT_FOUND_PROFESSIONAL.getDescription()
                            ))
                            .onItem().transformToUni(professional -> {
                                if (!professional.isActiveStatus()) {
                                    throw new BusinessException(ErrorType.BUSINESS_NOT_FOUND_PROFESSIONAL,
                                            ErrorType.BUSINESS_NOT_FOUND_PROFESSIONAL.getDescription());
                                }
                                System.out.println("Paso 4: " + dto);
                               return availableScheduleRepository.hasOverlap(professional.getId(), dto.date(), dto.startTime(), dto.endTime())
                                        .onItem().transformToUni(hasOverlap -> {
                                            if (!hasOverlap) {
                                                throw new BusinessException(ErrorType.SCHEDULE_NOT_AVAILABLE_ERROR,
                                                        ErrorType.SCHEDULE_NOT_AVAILABLE_ERROR.getDescription());
                                            }
                                           System.out.println("Paso 5: " + dto);
                                           return reservationRepository.hasActiveOverlap(professional.getId(), dto.date(), dto.startTime(), dto.endTime())
                                                    .onItem().transformToUni(existingOverlap -> {
                                                        if (existingOverlap) {
                                                            throw new BusinessException(ErrorType.SCHEDULE_RESERVATION_CONFLICT_ERROR,
                                                                    ErrorType.SCHEDULE_RESERVATION_CONFLICT_ERROR.getDescription());
                                                        }
                                                       System.out.println("createReservation: " + "que paso");

                                                        Reservation reservation = Reservation.builder()
                                                                .date(dto.date())
                                                                .startTime(dto.startTime())
                                                                .endTime(dto.endTime())
                                                                .customer((Customer) customer)
                                                                .professional((Professional) professional)
                                                                .status(ReservationStatus.CREATED)
                                                                .build();

                                                        return reservationRepository.persist(reservation)
                                                                .onItem().transform(saved -> {
                                                                    ApiResponse<ReservationDto> response = new ApiResponse<>();
                                                                    response.setData(reservationMapper.toDto(saved));
                                                                    return response;
                                                                });
                                                    });
                                        });
                            });
                });
    }

    @WithSession
    @Override
    public Uni<ApiResponse<Map<LocalDate, List<ReservationSummaryDto>>>> getReservationsByDate() {
        return reservationRepository.findAllReservations()
                .map(reservations -> {
                    Map<LocalDate, List<ReservationSummaryDto>> grouped = reservations.stream()
                            .filter(this::isActive)
                            .collect(Collectors.groupingBy(
                                    Reservation::getDate,
                                    TreeMap::new,
                                    Collectors.mapping(this::toReservationSummary, Collectors.toList())
                            ));

                    ApiResponse<Map<LocalDate, List<ReservationSummaryDto>>> response = new ApiResponse<>();
                    response.setData(grouped);
                    return response;
                });
    }

    private boolean isActive(Reservation reservation) {
        return reservation.getStatus() != null
                && reservation.getStatus() != org.mitocode.domain.enums.ReservationStatus.CANCELLED;
    }

    private ReservationSummaryDto toReservationSummary(Reservation reservation) {
        return new ReservationSummaryDto(
                reservation.getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName(),
                reservation.getProfessional().getFirstName() + " " + reservation.getProfessional().getLastName(),
                reservation.getStatus()
        );
    }
}


