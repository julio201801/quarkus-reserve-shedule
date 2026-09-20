package org.mitocode.domain.services;

import io.smallrye.mutiny.Uni;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.CustomerDto;
import org.mitocode.infrastructure.input.rest.dto.CustomerResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    Uni<ApiResponse<CustomerDto>> createCustomer(CustomerDto customerDto);

    Uni<ApiResponse<CustomerResponseDto>> findCustomerById(UUID id);

    Uni<ApiResponse<List<CustomerResponseDto>>> findAllCustomers();

    Uni<ApiResponse<CustomerDto>> updateCustomer(UUID id, CustomerDto customerDto);

    Uni<ApiResponse<Void>> deleteCustomer(UUID id);
}
