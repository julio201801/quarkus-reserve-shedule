package org.mitocode.infrastructure.output.persistence.repository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.domain.entities.Customer;
import org.mitocode.infrastructure.input.rest.dto.CustomerResponseDto;
import java.util.UUID;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer, UUID> {
    public Uni<CustomerResponseDto> findByCodeHQL(String code) {
        System.out.println("findByCodeHQL{code}"+code);
        return find("FROM Customer s where s.id = ?1", UUID.fromString(code))
                .singleResult()
                .map(customer -> new CustomerResponseDto(
                        customer.getId().toString(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.isActiveStatus()
                ));
    }
}
