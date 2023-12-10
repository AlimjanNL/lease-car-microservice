package nl.alimjan.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Long id);

    Optional<Customer> findCustomerByEmail(String email);
}
