package nl.alimjan.customer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.alimjan.customer.dto.AuthRequest;
import nl.alimjan.customer.dto.AuthResponse;
import nl.alimjan.customer.auth.AuthService;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerRegistrationRequest;
import nl.alimjan.customer.dto.CustomerUpdateRequest;
import nl.alimjan.customer.jwt.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  private final AuthService authService;


  public CustomerController(CustomerService customerService, AuthService authService) {
    this.customerService = customerService;
    this.authService = authService;
  }

  @GetMapping
  public ResponseEntity<List<CustomerDTO>> getCustomers() {
    log.info("Get request for all customers");

    List<CustomerDTO> customerDTOList = customerService.getAllCustomers();

    return ResponseEntity.status(HttpStatus.OK).body(customerDTOList);
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long customerId) {
    log.info(String.format("Get request for customer: [%s]", customerId));

    CustomerDTO customerDTO = customerService.getCustomerById(customerId);

    return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
  }

  @PostMapping("/register")
  public ResponseEntity<CustomerDTO> registerCustomer(
      @RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
    log.info(String.format("Post request for register new customer: [%s]",
        customerRegistrationRequest.getEmail()));

    String jwtToken = authService.generateToken(customerRegistrationRequest.getEmail(), "ROLE_USER");

    CustomerDTO customerDTO = customerService.addCustomer(customerRegistrationRequest);

    return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken).body(customerDTO);
  }

  @DeleteMapping("/{customerId}")
  public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
    log.info(String.format("Delete request for customer: [%s]", customerId));

    customerService.deleteCustomerById(customerId);

    return ResponseEntity.ok("Customer deleted successfully");
  }

  @PutMapping("/{customerId}")
  public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId,
      @RequestBody CustomerUpdateRequest customerUpdateRequest) {
    log.info(String.format("Put request for update customer: [%s]", customerId));

    CustomerDTO customerDTO = customerService.updateCustomer(customerId, customerUpdateRequest);

    return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
  }

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok()
        .header(HttpHeaders.AUTHORIZATION, response.getToken())
        .body(response);
  }

  @GetMapping("/validate")
  public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
    if (authService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.OK).build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
