package nl.alimjan.customer.auth;

import nl.alimjan.customer.Customer;
import nl.alimjan.customer.dto.AuthRequest;
import nl.alimjan.customer.dto.AuthResponse;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerDTOMapper;
import nl.alimjan.customer.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final CustomerDTOMapper customerDTOMapper;
  private final JwtUtil jwtUtil;

  public AuthService(AuthenticationManager authenticationManager,
      CustomerDTOMapper customerDTOMapper,
      JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.customerDTOMapper = customerDTOMapper;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse login(AuthRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
    Customer principal = (Customer) authentication.getPrincipal();
    CustomerDTO customerDTO = customerDTOMapper.apply(principal);
    String token = jwtUtil.generateToken(customerDTO.getEmail(), customerDTO.getRoles());
    return new AuthResponse(token, customerDTO);
  }

  public String generateToken(String username) {
    return jwtUtil.generateToken(username);
  }

  public String generateToken(String username, String... scopes) {
    return jwtUtil.generateToken(username, scopes);
  }

  public boolean validateToken(String token) {
    return jwtUtil.isTokenValid(token);
  }
}
