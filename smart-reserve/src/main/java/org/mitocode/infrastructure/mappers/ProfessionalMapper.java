package org.mitocode.infrastructure.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Professional;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalRequestDto;
@ApplicationScoped
public class ProfessionalMapper {

    public Professional toEntity(ProfessionalRequestDto dto) {
        if (dto == null) return null;

        return Professional.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .specialty(dto.specialty())
                .activeStatus(dto.activeStatus())
                .build();
    }

    public ProfessionalRequestDto toDto(Professional professional) {
        if (professional == null) return null;

        return new ProfessionalRequestDto(
                professional.getFirstName(),
                professional.getLastName(),
                professional.getSpecialty(),
                professional.isActiveStatus()
        );
    }
}
