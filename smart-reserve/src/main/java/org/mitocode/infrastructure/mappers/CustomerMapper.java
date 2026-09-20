package org.mitocode.infrastructure.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Customer;
import org.mitocode.infrastructure.input.rest.dto.CustomerDto;

@ApplicationScoped
public class CustomerMapper {
    public Customer toEntity(CustomerDto dto) {
        if (dto == null) return null;

        return Customer.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .activeStatus(dto.activeStatus())
                .build();
    }

    public CustomerDto toDto(Customer customer) {
        if (customer == null) return null;

        return new CustomerDto(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActiveStatus()
        );
    }
}


