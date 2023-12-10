package nl.alimjan.customer;

import java.util.List;
import java.util.stream.Collectors;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerDTOMapper;
import nl.alimjan.customer.dto.CustomerRegistrationRequest;
import nl.alimjan.customer.dto.CustomerUpdateRequest;
import nl.alimjan.utility.exception.DuplicateResourceException;
import nl.alimjan.utility.exception.RequestValidationException;
import nl.alimjan.utility.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerDao customerDao;
  private final CustomerDTOMapper customerDTOMapper;
  private final PasswordEncoder passwordEncoder;

  public CustomerService(
      @Qualifier("jpa") CustomerDao customerDao,
      CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
    this.customerDao = customerDao;
    this.customerDTOMapper = customerDTOMapper;
    this.passwordEncoder = passwordEncoder;
  }

  List<CustomerDTO> getAllCustomers() {
    return customerDao.selectAllCustomers().stream()
        .map(customerDTOMapper)
        .collect(Collectors.toList());
  }

  CustomerDTO getCustomerById(Long customerId) {
    return customerDao.selectCustomerById(customerId).map(customerDTOMapper).orElseThrow(
        () -> new ResourceNotFoundException(
            String.format("customer with customerId [%s] not found.", customerId)));
  }

  CustomerDTO addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

    String email = customerRegistrationRequest.getEmail();

    if (customerDao.isCustomerExistsWithEmail(email)) {
      throw new DuplicateResourceException("email already taken.");
    }

    Customer customer = getCustomerFromCustomerRegistrationRequest(customerRegistrationRequest);

    Customer addedCustomer = customerDao.insertCustomer(customer);

    return (addedCustomer != null) ? customerDTOMapper.apply(addedCustomer) : null;
  }

  private Customer getCustomerFromCustomerRegistrationRequest(
      CustomerRegistrationRequest customerRegistrationRequest) {
    Customer customer = new Customer();
    customer.setEmail(customerRegistrationRequest.getEmail());
    customer.setName(customerRegistrationRequest.getName());
    customer.setStreet(customerRegistrationRequest.getStreet());
    customer.setHousenumber(customerRegistrationRequest.getHousenumber());
    customer.setZipcode(customerRegistrationRequest.getZipcode());
    customer.setPlace(customerRegistrationRequest.getPlace());
    customer.setPhonenumber(customerRegistrationRequest.getPhonenumber());
    customer.setPassword(passwordEncoder.encode(customerRegistrationRequest.getPassword()));

    return customer;
  }

  void deleteCustomerById(Long id) {
    if (!customerDao.isCustomerExistsById(id)) {
      throw new ResourceNotFoundException(String.format("user with id [%s] not exists.", id));
    }

    customerDao.deleteCustomer(id);
  }

  CustomerDTO updateCustomer(Long id, CustomerUpdateRequest customerUpdateRequest) {
    Customer customer = customerDao.selectCustomerById(id).orElseThrow(
        () -> new ResourceNotFoundException(
            String.format("customer with customerId [%s] not found.", id)));

    boolean change = false;

    if (customerUpdateRequest.getName() != null && !customerUpdateRequest.getName()
        .equals(customer.getName())) {
      customer.setName(customerUpdateRequest.getName());
      change = true;
    }

    if (customerUpdateRequest.getEmail() != null && !customerUpdateRequest.getEmail()
        .equals(customer.getEmail())) {
      if (customerDao.isCustomerExistsWithEmail(customerUpdateRequest.getEmail())) {
        throw new DuplicateResourceException("email already taken.");
      }

      customer.setEmail(customerUpdateRequest.getEmail());
      change = true;
    }

    if (customerUpdateRequest.getStreet() != null && !customerUpdateRequest.getStreet()
        .equals(customer.getStreet())) {
      customer.setStreet(customerUpdateRequest.getStreet());
      change = true;
    }

    if (customerUpdateRequest.getHousenumber() != null && !customerUpdateRequest.getHousenumber()
        .equals(customer.getHousenumber())) {
      customer.setHousenumber(customerUpdateRequest.getHousenumber());
      change = true;
    }

    if (customerUpdateRequest.getZipcode() != null && !customerUpdateRequest.getZipcode()
        .equals(customer.getZipcode())) {
      customer.setZipcode(customerUpdateRequest.getZipcode());
      change = true;
    }

    if (customerUpdateRequest.getPlace() != null && !customerUpdateRequest.getPlace()
        .equals(customer.getPlace())) {
      customer.setPlace(customerUpdateRequest.getPlace());
      change = true;
    }

    if (customerUpdateRequest.getPhonenumber() != 0
        && customerUpdateRequest.getPhonenumber() != customer.getPhonenumber()) {
      customer.setPhonenumber(customerUpdateRequest.getPhonenumber());
      change = true;
    }

    if (!change) {
      throw new RequestValidationException("no data changes found.");
    }

    Customer updatedCustomer = customerDao.updateCustomer(customer);

    return (updatedCustomer != null) ? customerDTOMapper.apply(updatedCustomer) : null;
  }
}
