package org.mitocode.infrastructure.input.rest.resources;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.inject.Inject;

import org.mitocode.domain.services.ProfessionalService;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalRequestDto;
import org.mitocode.infrastructure.input.rest.dto.ProfessionalResponseDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/reserva/api/v1/professionals")
public class ProfessionalResource {
    private final ProfessionalService professionalService;
    @Inject
    public ProfessionalResource(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createProfessional(@Valid ProfessionalRequestDto request) {

        return professionalService.createProfessional(request)
                .map(resp -> Response.status(201).entity(resp).build());
    }
    @GET
    @Path("/{professionalId}")
    public Uni<Response> searchShipmentByCode(@PathParam("professionalId") UUID professionalId) {

        return professionalService.findProfessionalById(professionalId)
                .map(resp -> Response.status(200).entity(resp).build());
    }
    @GET
    public Uni<ApiResponse<List<ProfessionalResponseDto>>> findAllProfessionals() {
        return professionalService.findAllProfessionals();
    }
    @PUT
    @Path("/{id}")
    public Uni<ApiResponse<ProfessionalRequestDto>> updateProfessional(
            @PathParam("id") UUID id,
            ProfessionalRequestDto professionalRequestDto) {

        return professionalService.updateProfessional(id, professionalRequestDto);
    }
    @DELETE
    @Path("/{id}")
    public Uni<ApiResponse<Void>> deleteProfessional(
            @PathParam("id") UUID id) {

        return professionalService.deleteProfessional(id);
    }
}
