package nl.alimjan.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CustomerRepositoryTest {

  @Autowired
  private CustomerRepository customerRepository;

  @BeforeEach
  void beforeEach() {
    // Given
    Customer customer = new Customer();
    customer.setEmail("john.doe@test.com");
    customer.setName("John doe");
    customer.setStreet("123 Main Street");
    customer.setHousenumber("45a");
    customer.setZipcode("12345");
    customer.setPlace("City");
    customer.setPhonenumber(123456789);
    customer.setPassword("12345");

    customerRepository.save(customer);
  }

  @AfterEach
  public void afterEach() {
    customerRepository.deleteAll();
  }

  @Test
  void existsCustomerByEmail() {
    // When
    boolean expectCustomer = customerRepository.existsCustomerByEmail("john.doe@test.com");

    //Then
    assertThat(expectCustomer).isTrue();
  }

  @Test
  void existsCustomerByEmailWhenEmailNotPresent() {
    // When
    boolean expectCustomer = customerRepository.existsCustomerByEmail("wrongemail@test.com");

    //Then
    assertThat(expectCustomer).isFalse();
  }

  @Test
  void existsCustomerById() {
    // When
    boolean expectCustomer = customerRepository.existsCustomerById(1L);

    //Then
    assertThat(expectCustomer).isTrue();
  }

  @Test
  void existsCustomerByIdWhenIdNotPresent() {
    // When
    boolean expectCustomer = customerRepository.existsCustomerById(-1L);

    //Then
    assertThat(expectCustomer).isFalse();
  }
}