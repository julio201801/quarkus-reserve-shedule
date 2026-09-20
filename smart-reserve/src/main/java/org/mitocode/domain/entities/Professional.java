package org.mitocode.domain.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "professional")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professional extends PanacheEntityBaseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name="first_name",  unique = true, nullable = false)
    private String firstName;
    @Column(name="last_name",nullable = false)
    private String lastName;
    @Column(name="specialty",nullable = false)
    private String specialty;
    @Column(name="active_status", nullable = false)
    private boolean activeStatus;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
