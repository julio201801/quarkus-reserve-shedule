package org.mitocode.infrastructure.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Professional;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalRequestDto;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalResponseDto;

import java.util.UUID;

@ApplicationScoped
public class ProfessionalRepository implements PanacheRepositoryBase<Professional, UUID> {
    public Uni<ProfessionalResponseDto> findByCodeHQL(String code) {
        return find("FROM Professional s where s.id = ?1", UUID.fromString(code))
                .singleResult()
                .map(professional -> new ProfessionalResponseDto(
                        professional.getId().toString(),
                        professional.getFirstName(),
                        professional.getLastName(),
                        professional.getSpecialty(),
                        professional.isActiveStatus()
                ));
    }
}