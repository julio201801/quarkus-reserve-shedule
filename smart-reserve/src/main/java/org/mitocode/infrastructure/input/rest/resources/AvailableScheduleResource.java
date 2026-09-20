package org.mitocode.infrastructure.input.rest.resources;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.inject.Inject;
import org.mitocode.domain.services.AvailableScheduleService;
import org.mitocode.infrastructure.input.rest.dto.AvailableScheduleDto;

@Slf4j
@Path("/reserva/api/v1/available-schedules")
public class AvailableScheduleResource {
    private final AvailableScheduleService availableScheduleService;

    @Inject
    public AvailableScheduleResource(AvailableScheduleService availableScheduleService) {
        this.availableScheduleService = availableScheduleService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createAvailableSchedule(@Valid AvailableScheduleDto request) {
        return availableScheduleService.save(request)
                .map(resp -> Response.status(201).entity(resp).build());
    }
}
