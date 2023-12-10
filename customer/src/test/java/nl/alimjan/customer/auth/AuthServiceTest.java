package nl.alimjan.customer.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import nl.alimjan.customer.Customer;
import nl.alimjan.customer.dto.AuthRequest;
import nl.alimjan.customer.dto.AuthResponse;
import nl.alimjan.customer.dto.CustomerDTO;
import nl.alimjan.customer.dto.CustomerDTOMapper;
import nl.alimjan.customer.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class AuthServiceTest {

  @Test
  void login_ValidCredentials_ReturnsAuthResponseWithToken() {
    String username = "testuser";
    String password = "testpassword";
    AuthRequest authRequest = new AuthRequest(username, password);

    Customer customer = new Customer();
    customer.setEmail(username);

    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setEmail(username);
    customerDTO.setRoles(Collections.singletonList("ROLE_USER"));

    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    CustomerDTOMapper customerDTOMapper = mock(CustomerDTOMapper.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);

    when(authenticationManager.authenticate(any()))
        .thenReturn(new UsernamePasswordAuthenticationToken(customer, null));
    when(customerDTOMapper.apply(customer)).thenReturn(customerDTO);
    when(jwtUtil.generateToken(username, customerDTO.getRoles())).thenReturn("mockedToken");

    AuthService authService = new AuthService(authenticationManager, customerDTOMapper, jwtUtil);

    AuthResponse authResponse = authService.login(authRequest);

    assertNotNull(authResponse);
    assertEquals("mockedToken", authResponse.getToken());
    assertEquals(customerDTO, authResponse.getCustomerDTO());

    verify(authenticationManager, times(1)).authenticate(any());
    verify(customerDTOMapper, times(1)).apply(customer);
    verify(jwtUtil, times(1)).generateToken(username, customerDTO.getRoles());
  }

  @Test
  void generateToken_Username_ReturnsToken() {
    String username = "testuser";
    JwtUtil jwtUtil = mock(JwtUtil.class);
    when(jwtUtil.generateToken(username)).thenReturn("mockedToken");

    AuthService authService = new AuthService(null, null, jwtUtil);

    String token = authService.generateToken(username);

    assertEquals("mockedToken", token);

    verify(jwtUtil, times(1)).generateToken(username);
  }

  @Test
  void generateToken_UsernameAndScopes_ReturnsToken() {
    // Arrange
    String username = "testuser";
    String[] scopes = {"SCOPE_READ", "SCOPE_WRITE"};
    JwtUtil jwtUtil = mock(JwtUtil.class);
    when(jwtUtil.generateToken(username, scopes)).thenReturn("mockedToken");

    AuthService authService = new AuthService(null, null, jwtUtil);

    String token = authService.generateToken(username, scopes);

    assertEquals("mockedToken", token);

    verify(jwtUtil, times(1)).generateToken(username, scopes);
  }

  @Test
  void validateToken_ValidToken_ReturnsTrue() {
    String token = "validToken";
    JwtUtil jwtUtil = mock(JwtUtil.class);
    when(jwtUtil.isTokenValid(token)).thenReturn(true);

    AuthService authService = new AuthService(null, null, jwtUtil);

    boolean isValid = authService.validateToken(token);

    assertTrue(isValid);

    verify(jwtUtil, times(1)).isTokenValid(token);
  }
}