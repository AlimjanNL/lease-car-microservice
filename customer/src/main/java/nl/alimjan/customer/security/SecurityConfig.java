package nl.alimjan.customer.security;

import nl.alimjan.customer.CustomerDetailsService;
import nl.alimjan.customer.jwt.JwtFilter;
import nl.alimjan.customer.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomerDetailsService customerDetailsService;
  private final JwtUtil jwtUtil;

  public SecurityConfig(CustomerDetailsService customerDetailsService, JwtUtil jwtUtil) {
    this.customerDetailsService = customerDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration configuration
  ) throws Exception {
    return configuration.getAuthenticationManager();
  }

  public JwtFilter jwtFilter() {
    return new JwtFilter(customerDetailsService, jwtUtil);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customerDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/v1/customers/register").permitAll()
        .antMatchers(HttpMethod.POST, "/api/v1/customers/login").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/customers/validate").permitAll()
        .antMatchers("/api/v1/customers/**").authenticated()
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
  }

}
