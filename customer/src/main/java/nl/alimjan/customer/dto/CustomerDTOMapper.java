package nl.alimjan.customer.dto;

import java.util.function.Function;
import java.util.stream.Collectors;
import nl.alimjan.customer.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CustomerDTOMapper implements Function<Customer, CustomerDTO> {

  @Override
  public CustomerDTO apply(Customer customer) {
    return new CustomerDTO(
        customer.getName(),
        customer.getEmail(),
        customer.getStreet(),
        customer.getHousenumber(),
        customer.getZipcode(),
        customer.getPlace(),
        customer.getPhonenumber(),
        customer.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
  }
}
