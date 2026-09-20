package org.mitocode.aplication;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.entities.Professional;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.services.ProfessionalService;
import org.mitocode.infrastructure.error.exceptions.BusinessException;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalRequestDto;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalResponseDto;
import org.mitocode.infrastructure.output.persistence.repository.ProfessionalRepository;
import org.mitocode.infrastructure.mappers.ProfessionalMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ProfessionalServiceUseCase implements ProfessionalService {
    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    public ProfessionalServiceUseCase(ProfessionalRepository professionalRepository, ProfessionalMapper professionalMapper) {
        this.professionalRepository = professionalRepository;
        this.professionalMapper = professionalMapper;
    }
    @WithTransaction
    @Override
    public Uni<ApiResponse<ProfessionalRequestDto>> createProfessional(ProfessionalRequestDto dto) {

        Professional professional = professionalMapper.toEntity(dto);
        return professionalRepository.persist(professional)
                .replaceWith(() -> {
                    ApiResponse<ProfessionalRequestDto> response = new ApiResponse<>();
                    response.setData(professionalMapper.toDto(professional));
                    return response;
                });
    }
    @WithSession
    @Override
    public Uni<ApiResponse<ProfessionalResponseDto>> findProfessionalById(UUID id) {
        return professionalRepository.findByCodeHQL(id.toString())
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transform(dto -> {
                    ApiResponse<ProfessionalResponseDto> response = new ApiResponse<>();
                    response.setData(new ProfessionalResponseDto(dto.id().toString(), dto.firstName(), dto.lastName(), dto.specialty(), dto.activeStatus()));
                    return response;
                });
    }
    @WithSession
    @Override
    public Uni<ApiResponse<List<ProfessionalResponseDto>>> findAllProfessionals() {
        return professionalRepository.findAll().list()
                .onItem()
                .transform(list -> {
                    List<ProfessionalResponseDto> dtoList = list.stream()
                            .map(professional -> new ProfessionalResponseDto(
                                    professional.getId().toString(),
                                    professional.getFirstName(),
                                    professional.getLastName(),
                                    professional.getSpecialty(),
                                    professional.isActiveStatus()
                            ))
                            .toList();

                    ApiResponse<List<ProfessionalResponseDto>> response = new ApiResponse<>();
                    response.setData(dtoList);

                    return response;
                });
    }

    @WithTransaction
    @Override
    public Uni<ApiResponse<ProfessionalRequestDto>> updateProfessional(UUID id, ProfessionalRequestDto professionalRequestDto) {
        return professionalRepository.findById(id)
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transformToUni(professional -> {
                    // Actualizar los campos
                    professional.setFirstName(professionalRequestDto.firstName());
                    professional.setLastName(professionalRequestDto.lastName());
                    professional.setSpecialty(professionalRequestDto.specialty());
                    professional.setActiveStatus(professionalRequestDto.activeStatus());

                    // Persistir cambios
                    return professionalRepository.persist(professional)
                            .onItem()
                            .transform(updated -> {
                                ApiResponse<ProfessionalRequestDto> response = new ApiResponse<>();
                                response.setData(new ProfessionalRequestDto(
                                        updated.getFirstName(),
                                        updated.getLastName(),
                                        updated.getSpecialty(),
                                        updated.isActiveStatus()
                                ));
                                return response;
                            });
                });
    }
    @WithTransaction
    @Override
    public Uni<ApiResponse<Void>> deleteProfessional(UUID id) {
        return professionalRepository.findById(id)
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transformToUni(professional ->
                        professionalRepository.delete(professional)
                                .replaceWith(() -> {
                                    ApiResponse<Void> response = new ApiResponse<>();
                                    response.setData(null); // no hay datos, solo confirmación
                                    return response;
                                })
                );
    }
}
