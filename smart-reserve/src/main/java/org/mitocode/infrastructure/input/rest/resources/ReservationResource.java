package org.mitocode.infrastructure.input.rest.resources;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.inject.Inject;
import org.mitocode.domain.services.ReservationService;
import org.mitocode.infrastructure.input.rest.dto.ReservationDto;

import java.util.UUID;

@Slf4j
@Path("/reserva/api/v1/reservations")
public class ReservationResource {
    private final ReservationService reservationService;

    @Inject
    public ReservationResource(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createReservation(@Valid ReservationDto request) {
        System.out.println("createReservation: " + request);
        return reservationService.createReservation(request)
                .map(response -> Response.status(201).entity(response).build());
    }

    @DELETE
    @Path("/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> cancelReservation(@PathParam("reservationId") UUID reservationId) {
        return reservationService.cancelReservation(reservationId)
                .map(response -> Response.status(200).entity(response).build());
    }

    @GET
    @Path("/reservations-by-date")
    public Uni<Response> getReservationsByDate() {

        return reservationService.getReservationsByDate()
                .map(response ->
                        Response.ok(response).build()
                );
    }
}
