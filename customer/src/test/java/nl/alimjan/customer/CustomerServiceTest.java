package nl.alimjan.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerDTOMapper;
import nl.alimjan.customer.dto.CustomerRegistrationRequest;
import nl.alimjan.customer.dto.CustomerUpdateRequest;
import nl.alimjan.utility.exception.DuplicateResourceException;
import nl.alimjan.utility.exception.RequestValidationException;
import nl.alimjan.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class CustomerServiceTest {

  private CustomerService customerService;
  @Mock
  private CustomerDao customerDao;
  @Mock
  private PasswordEncoder passwordEncoder;
  AutoCloseable autoCloseable;
  private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    customerService = new CustomerService(customerDao, customerDTOMapper, passwordEncoder);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getAllCustomers() {
    customerService.getAllCustomers();

    Mockito.verify(customerDao).selectAllCustomers();
  }

  @Test
  void getCustomerById() {
    // Given
    Customer customer = this.getTestCustomer();
    when(customerDao.selectCustomerById(customer.getId())).thenReturn(Optional.of(customer));

    CustomerDTO customerDTO = customerDTOMapper.apply(customer);

    // When
    CustomerDTO testCustomer = customerService.getCustomerById(customer.getId());

    // Assert
    assertThat(testCustomer).isEqualTo(customerDTO);
  }

  @Test
  void addCustomer() {
    // Given
    String email = "john.doe@test.com";

    when(customerDao.isCustomerExistsWithEmail(email)).thenReturn(false);

    CustomerRegistrationRequest request = this.getCustomerRegistrationRequest(email);

    // When
    customerService.addCustomer(request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getId()).isNull();
    assertThat(cpturedCustomer.getName()).isEqualTo(request.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(request.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(request.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(request.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(request.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(request.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(request.getPhonenumber());
  }

  @Test
  void throwExceptionWhenEmailExistWhileAddingCustomer() {
    // Given
    String email = "john.doe@test.com";

    when(customerDao.isCustomerExistsWithEmail(email)).thenReturn(true);

    CustomerRegistrationRequest request = this.getCustomerRegistrationRequest(email);

    // When
    assertThatThrownBy(() -> customerService.addCustomer(request)).isInstanceOf(
        DuplicateResourceException.class).hasMessage("email already taken.");

    // Assert
    verify(customerDao, never()).insertCustomer(any());
  }

  @Test
  void deleteCustomer() {
    // Given
    Long id = 1L;

    when(customerDao.isCustomerExistsById(id)).thenReturn(true);

    // When
    customerService.deleteCustomerById(id);

    // Then
    verify(customerDao).deleteCustomer(id);
  }

  @Test
  void throwExceptionWhenCustomerNotExistWhileDeletingCustomer() {
    // Given
    Long id = 1L;

    when(customerDao.isCustomerExistsById(id)).thenReturn(false);

    // When
    assertThatThrownBy(() -> customerService.deleteCustomerById(id)).isInstanceOf(
            ResourceNotFoundException.class)
        .hasMessage(String.format("user with id [%s] not exists.", id));

    // Then
    verify(customerDao, never()).deleteCustomer(id);
  }

  @Test
  void updateCustomerAllFields() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alex.malex@test.com";

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setName("John Doe");
    updateRequest.setEmail(newEmail);
    updateRequest.setStreet("123 Main Street");
    updateRequest.setHousenumber("45a");
    updateRequest.setZipcode("12345");
    updateRequest.setPlace("City");
    updateRequest.setPhonenumber(123456789);

    when(customerDao.isCustomerExistsWithEmail(newEmail)).thenReturn(false);

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(updateRequest.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(updateRequest.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(updateRequest.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(updateRequest.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(updateRequest.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(updateRequest.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(updateRequest.getPhonenumber());
  }

  @Test
  void updateCustomerName() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setName("Alex Doe");

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(updateRequest.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerEmail() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alex.doe@test.com";
    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setEmail(newEmail);

    when(customerDao.isCustomerExistsWithEmail(newEmail)).thenReturn(false);

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(updateRequest.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerStreet() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setStreet("second main street");

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(updateRequest.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerHouseNumber() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setHousenumber("67b");

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(updateRequest.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerZipcode() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setZipcode("54321AB");

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(updateRequest.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerPlace() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setPlace("second city");

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(updateRequest.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(customer.getPhonenumber());
  }

  @Test
  void updateCustomerPhoneNumber() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setPhonenumber(987654321);

    // When
    customerService.updateCustomer(id, updateRequest);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

    Customer cpturedCustomer = customerArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCustomer.getName()).isEqualTo(customer.getName());
    assertThat(cpturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(cpturedCustomer.getStreet()).isEqualTo(customer.getStreet());
    assertThat(cpturedCustomer.getHousenumber()).isEqualTo(customer.getHousenumber());
    assertThat(cpturedCustomer.getZipcode()).isEqualTo(customer.getZipcode());
    assertThat(cpturedCustomer.getPlace()).isEqualTo(customer.getPlace());
    assertThat(cpturedCustomer.getPhonenumber()).isEqualTo(updateRequest.getPhonenumber());
  }

  @Test
  void throwExceptionWhenCustomerUpdateHasNoChanges() {
    // Given
    Long id = 1L;
    Customer customer = this.getTestCustomer();

    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alex.malex@test.com";

    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
    updateRequest.setName(customer.getName());
    updateRequest.setEmail(customer.getEmail());
    updateRequest.setStreet(customer.getStreet());
    updateRequest.setHousenumber(customer.getHousenumber());
    updateRequest.setZipcode(customer.getZipcode());
    updateRequest.setPlace(customer.getPlace());
    updateRequest.setPhonenumber(customer.getPhonenumber());

    // When
    assertThatThrownBy(() -> customerService.updateCustomer(id, updateRequest)).isInstanceOf(
        RequestValidationException.class).hasMessage("no data changes found.");

    // Then
    verify(customerDao, never()).updateCustomer(any());
  }

  private Customer getTestCustomer() {
    Customer customer = new Customer();
    customer.setEmail("john.doe@test.com");
    customer.setName("John doe");
    customer.setStreet("123 Main Street");
    customer.setHousenumber("45a");
    customer.setZipcode("12345");
    customer.setPlace("City");
    customer.setPhonenumber(123456789);

    return customer;
  }

  private CustomerRegistrationRequest getCustomerRegistrationRequest(String email) {
    CustomerRegistrationRequest request = new CustomerRegistrationRequest();
    request.setName("John Doe");
    request.setEmail(email);
    request.setStreet("123 Main Street");
    request.setHousenumber("45a");
    request.setZipcode("12345");
    request.setPlace("City");
    request.setPhonenumber(123456789);

    return request;
  }

}