package nl.alimjan.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

  List<Customer> selectAllCustomers();

  Optional<Customer> selectCustomerById(Long id);

  Customer insertCustomer(Customer customer);

  boolean isCustomerExistsWithEmail(String email);

  boolean isCustomerExistsById(Long id);

  void deleteCustomer(Long id);

  Customer updateCustomer(Customer customer);

  Optional<Customer> findCustomerByEmail(String email);
}
