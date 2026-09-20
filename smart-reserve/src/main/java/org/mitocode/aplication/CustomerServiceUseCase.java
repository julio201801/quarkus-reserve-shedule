package org.mitocode.aplication;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.entities.Customer;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.services.CustomerService;
import org.mitocode.infrastructure.error.exceptions.BusinessException;
import org.mitocode.infrastructure.input.rest.dto.ApiResponse;
import org.mitocode.infrastructure.input.rest.dto.CustomerDto;
import org.mitocode.infrastructure.input.rest.dto.CustomerResponseDto;
import org.mitocode.infrastructure.mappers.CustomerMapper;
import org.mitocode.infrastructure.output.persistence.repository.CustomerRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class CustomerServiceUseCase implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    @WithTransaction
    @Override
    public Uni<ApiResponse<CustomerDto>> createCustomer(CustomerDto dto) {

        Customer customer = customerMapper.toEntity(dto);
        return customerRepository.persist(customer)
                .replaceWith(() -> {
                    ApiResponse<CustomerDto> response = new ApiResponse<>();
                    response.setData(customerMapper.toDto(customer));
                    return response;
                });
    }
    @WithSession
    @Override
    public Uni<ApiResponse<CustomerResponseDto>> findCustomerById(UUID id) {
        return customerRepository.findByCodeHQL(id.toString())
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transform(dto -> {
                    ApiResponse<CustomerResponseDto> response = new ApiResponse<>();
                    response.setData(dto);
                    return response;
                });
    }
    @WithSession
    @Override
    public Uni<ApiResponse<List<CustomerResponseDto>>> findAllCustomers() {
        return customerRepository.findAll().list()
                .onItem()
                .transform(list -> {
                    List<CustomerResponseDto> dtoList = list.stream()
                            .map(customer -> new CustomerResponseDto(
                                    customer.getId().toString(),
                                    customer.getFirstName(),
                                    customer.getLastName(),
                                    customer.getEmail(),
                                    customer.getPhone(),
                                    customer.isActiveStatus()
                            ))
                            .toList();

                    ApiResponse<List<CustomerResponseDto>> response = new ApiResponse<>();
                    response.setData(dtoList);

                    return response;
                });
    }

    @WithTransaction
    @Override
    public Uni<ApiResponse<CustomerDto>> updateCustomer(UUID id, CustomerDto customerDto) {
        return customerRepository.findById(id)
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transformToUni(customer -> {
                    // Actualizar los campos
                    customer.setFirstName(customerDto.firstName());
                    customer.setLastName(customerDto.lastName());
                    customer.setEmail(customerDto.email());
                    customer.setPhone(customerDto.phone());
                    customer.setActiveStatus(customerDto.activeStatus());

                    // Persistir cambios
                    return customerRepository.persist(customer)
                            .onItem()
                            .transform(updated -> {
                                ApiResponse<CustomerDto> response = new ApiResponse<>();
                                response.setData(new CustomerDto(
                                        updated.getFirstName(),
                                        updated.getLastName(),
                                        updated.getEmail(),
                                        updated.getPhone(),
                                        updated.isActiveStatus()
                                ));
                                return response;
                            });
                });
    }
    @WithTransaction
    @Override
    public Uni<ApiResponse<Void>> deleteCustomer(UUID id) {
        return customerRepository.findById(id)
                .onItem()
                .ifNull()
                .failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                ))
                .onItem()
                .transformToUni(customer ->
                        customerRepository.delete(customer)
                                .replaceWith(() -> {
                                    ApiResponse<Void> response = new ApiResponse<>();
                                    response.setData(null); // no hay datos, solo confirmación
                                    return response;
                                })
                );
    }
}
