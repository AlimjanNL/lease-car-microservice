package nl.alimjan.customer;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.alimjan.customer.auth.AuthService;
import nl.alimjan.customer.dto.AuthRequest;
import nl.alimjan.customer.dto.AuthResponse;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerRegistrationRequest;
import nl.alimjan.customer.dto.CustomerUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

  @Mock
  private CustomerService customerService;
  @Mock
  private AuthService authService;
  @InjectMocks
  private CustomerController customerController;
  private final ObjectMapper objectMapper = new ObjectMapper();
  AutoCloseable autoCloseable;

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    customerController = new CustomerController(customerService, authService);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getCustomers() throws Exception {
    List<CustomerDTO> customerList = Arrays.asList(
        new CustomerDTO("John Doe", "john@example.com", "Street", "123", "12345", "City",
            1234567890, Collections.singletonList("ROLE_TEST")),
        new CustomerDTO("Jane Doe", "jane@example.com", "Avenue", "456", "67890", "Town", 987654321,
            Collections.singletonList("ROLE_TEST"))
    );

    when(customerService.getAllCustomers()).thenReturn(customerList);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(get("/api/v1/customers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[1].name").value("Jane Doe"));

    verify(customerService, times(1)).getAllCustomers();
    verifyNoMoreInteractions(customerService);
  }

  @Test
  void getCustomer() throws Exception {
    CustomerDTO customerDTO = new CustomerDTO("John Doe", "john@example.com", "Street", "123",
        "12345", "City", 1234567890, Collections.singletonList("ROLE_TEST"));
    Long customerId = 1L;

    when(customerService.getCustomerById(customerId)).thenReturn(customerDTO);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(get("/api/v1/customers/{customerId}", customerId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.name").value("John Doe"));

    verify(customerService, times(1)).getCustomerById(customerId);
    verifyNoMoreInteractions(customerService);
  }

  @Test
  void registerCustomer() throws Exception {
    CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest();
    registrationRequest.setName("John Doe");
    registrationRequest.setEmail("john@example.com");
    registrationRequest.setStreet("Street");
    registrationRequest.setHousenumber("123");
    registrationRequest.setZipcode("12345");
    registrationRequest.setPlace("City");
    registrationRequest.setPhonenumber(1234567890);

    String mockToken = "mockToken";
    when(authService.generateToken(anyString(), eq("ROLE_USER"))).thenReturn(mockToken);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(post("/api/v1/customers/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(registrationRequest)))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + mockToken));

    verify(customerService, times(1)).addCustomer(registrationRequest);
    verifyNoMoreInteractions(customerService);
  }

  @Test
  void deleteCustomer() throws Exception {
    Long customerId = 1L;

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(delete("/api/v1/customers/{customerId}", customerId))
        .andExpect(status().isOk());

    verify(customerService, times(1)).deleteCustomerById(customerId);
    verifyNoMoreInteractions(customerService);
  }

  @Test
  void updateCustomer() throws Exception {
    Long customerId = 1L;
    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setName("Updated Name");

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(put("/api/v1/customers/{customerId}", customerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(updateRequest)))
        .andExpect(status().isOk());

    verify(customerService, times(1)).updateCustomer(customerId, updateRequest);
    verifyNoMoreInteractions(customerService);
  }

  @Test
  void login() throws Exception {
    AuthRequest authRequest = new AuthRequest("john@example.com", "password");
    AuthResponse authResponse = new AuthResponse("Bearer mockToken",
        new CustomerDTO("John Doe", "john@example.com", "Street", "123", "12345", "City",
            1234567890, Collections.singletonList("ROLE_TEST")));

    when(authService.login(authRequest)).thenReturn(authResponse);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(post("/api/v1/customers/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.AUTHORIZATION, authResponse.getToken()))
        .andExpect(jsonPath("$.customerDTO.name").value("John Doe"));

    verify(authService, times(1)).login(authRequest);
    verifyNoMoreInteractions(authService);
  }

  @Test
  void validateToken() throws Exception {
    String mockToken = "mockToken";

    when(authService.validateToken(mockToken)).thenReturn(true);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

    mockMvc.perform(get("/api/v1/customers/validate").param("token", mockToken))
        .andExpect(status().isOk());

    verify(authService, times(1)).validateToken(mockToken);
    verifyNoMoreInteractions(authService);
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}