package org.mitocode.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "customer")
@Setter
@Getter
@NoArgsConstructor      // Constructor vacío
@AllArgsConstructor     // Constructor con todos los campos
@Builder                // Permite crear objetos con el patrón Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name="first_name",  unique = true, nullable = false)
    private String firstName;
    @Column(name="last_name",nullable = false)
    private String lastName;
    @Column(name="email", unique = true, nullable = false)
    private String email;
    @Column(name="phone")
    private String phone;
    @Column(name="active_status")
    private boolean activeStatus;

}
