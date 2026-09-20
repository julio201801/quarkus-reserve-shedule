package org.mitocode.infrastructure.input.rest.resources;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.inject.Inject;
import org.mitocode.domain.services.CustomerService;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.CustomerDto;
import org.mitocode.infrastructure.input.rest.dto.CustomerResponseDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/reserva/api/v1/customers")

public class CustomerResource {
    private final CustomerService customerService;
    @Inject
    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createCustomer(@Valid CustomerDto request) {

        return customerService.createCustomer(request)
                .map(resp -> Response.status(201).entity(resp).build());
    }
    @GET
    @Path("/{customerId}")
    public Uni<Response> searchCustomerById(@PathParam("customerId") UUID customerId) {

        return customerService.findCustomerById(customerId)
                .map(resp -> Response.status(200).entity(resp).build());
    }
    @GET
    public Uni<ApiResponse<List<CustomerResponseDto>>> findAllCustomers() {
        return customerService.findAllCustomers();
    }
    @PUT
    @Path("/{id}")
    public Uni<ApiResponse<CustomerDto>> updateCustomer(
            @PathParam("id") UUID id,
            CustomerDto customerDto) {

        return customerService.updateCustomer(id, customerDto);
    }
    @DELETE
    @Path("/{id}")
    public Uni<ApiResponse<Void>> deleteCustomer(
            @PathParam("id") UUID id) {

        return customerService.deleteCustomer(id);
    }
}
