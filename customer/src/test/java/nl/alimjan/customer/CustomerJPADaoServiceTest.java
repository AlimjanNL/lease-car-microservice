package nl.alimjan.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CustomerJPADaoServiceTest {

  private CustomerJPADaoService customerJPADaoService;

  @Mock
  private CustomerRepository customerRepository;

  private AutoCloseable autoCloseable;

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    customerJPADaoService = new CustomerJPADaoService(customerRepository);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  void selectAllCustomers() {
    customerJPADaoService.selectAllCustomers();

    Mockito.verify(customerRepository).findAll();
  }

  @Test
  void selectCustomerById() {
    Long id = 1L;

    customerJPADaoService.selectCustomerById(id);

    Mockito.verify(customerRepository).findById(id);
  }

  @Test
  void insertCustomer() {
    Customer customer = this.getTestCustomer();

    customerJPADaoService.insertCustomer(customer);

    Mockito.verify(customerRepository).save(customer);
  }

  @Test
  void isCustomerExistsWithEmail() {
    String email = "john.doe@test.com";

    customerJPADaoService.isCustomerExistsWithEmail(email);

    Mockito.verify(customerRepository).existsCustomerByEmail(email);
  }

  @Test
  void isCustomerExistsById() {
    Long id = 1L;

    customerJPADaoService.isCustomerExistsById(id);

    Mockito.verify(customerRepository).existsCustomerById(id);
  }

  @Test
  void deleteCustomer() {
    Long id = 1L;

    customerJPADaoService.deleteCustomer(id);

    Mockito.verify(customerRepository).deleteById(id);
  }

  @Test
  void updateCustomer() {
    Customer customer = this.getTestCustomer();

    customerJPADaoService.updateCustomer(customer);

    Mockito.verify(customerRepository).save(customer);
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
}