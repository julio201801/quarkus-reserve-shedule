package org.mitocode.domain.services;

import io.smallrye.mutiny.Uni;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalRequestDto;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProfessionalService {

    Uni<ApiResponse<ProfessionalRequestDto>> createProfessional(ProfessionalRequestDto professionalRequestDto);

    Uni<ApiResponse<ProfessionalResponseDto>> findProfessionalById(UUID id);

    Uni<ApiResponse<List<ProfessionalResponseDto>>> findAllProfessionals();

    Uni<ApiResponse<ProfessionalRequestDto>> updateProfessional(UUID id, ProfessionalRequestDto professionalRequestDto);

    Uni<ApiResponse<Void>> deleteProfessional(UUID id);
}
