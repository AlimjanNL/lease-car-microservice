package nl.alimjan.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.alimjan.customer.dto.CustomerDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

  String token;
  CustomerDTO customerDTO;
}
