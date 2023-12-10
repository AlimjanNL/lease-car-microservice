package nl.alimjan.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

  private final CustomerDao customerDao;

  public CustomerDetailsService(@Qualifier("jpa") CustomerDao customerDao) {
    this.customerDao = customerDao;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return customerDao.findCustomerByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException("User not found with email: " + email)
    );
  }
}
