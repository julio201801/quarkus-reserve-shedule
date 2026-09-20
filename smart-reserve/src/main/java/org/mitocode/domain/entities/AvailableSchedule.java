package org.mitocode.domain.entities;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Table(name = "available_schedule")
@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AvailableSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "profesional_id", nullable = false)
    private Professional professional;
    @Column(nullable = false)
    private LocalDate date;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "active_status", nullable = false)
    private boolean activeStatus;

    protected AvailableSchedule() {
    }

    private AvailableSchedule(
            Professional professional,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            boolean activeStatus) {

        if (professional == null) {
            throw new IllegalArgumentException(
                    "Professional is required");
        }

        if (date == null) {
            throw new IllegalArgumentException(
                    "Date is required");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Start and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        this.professional = professional;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activeStatus = activeStatus;
    }

    public static AvailableSchedule create(
            Professional professional,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            boolean activeStatus) {

        return new AvailableSchedule(
                professional,
                date,
                startTime,
                endTime,
                activeStatus);
    }

    public boolean overlapsWith(AvailableSchedule other) {

        if (!this.professional.getId()
                .equals(other.professional.getId())) {
            return false;
        }

        if (!this.date.equals(other.date)) {
            return false;
        }

        return this.startTime.isBefore(other.endTime)
                && this.endTime.isAfter(other.startTime);
    }
}
