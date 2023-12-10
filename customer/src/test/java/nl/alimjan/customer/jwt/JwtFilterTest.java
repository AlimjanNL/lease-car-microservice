package nl.alimjan.customer.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import nl.alimjan.customer.CustomerDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtFilterTest {
  @Mock
  private CustomerDetailsService customerDetailsService;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private JwtFilter jwtFilter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtFilter = new JwtFilter(customerDetailsService, jwtUtil);
  }

  @Test
  void doFilterInternal() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer mockToken");

    when(jwtUtil.getSubject("mockToken")).thenReturn("testUser");
    when(jwtUtil.isTokenValid("mockToken")).thenReturn(true);

    UserDetails userDetails = User.withUsername("testUser")
        .password("password")
        .roles("USER")
        .build();
    when(customerDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

    MockFilterChain filterChain = new MockFilterChain();

    jwtFilter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);

    verify(customerDetailsService, times(1)).loadUserByUsername("testUser");
    verifyNoMoreInteractions(customerDetailsService);
  }
}