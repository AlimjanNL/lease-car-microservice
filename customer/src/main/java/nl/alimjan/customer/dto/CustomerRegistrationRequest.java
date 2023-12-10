package nl.alimjan.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRegistrationRequest {

  public String name;
  public String email;
  public String street;
  public String housenumber;
  public String zipcode;
  public String place;
  public int phonenumber;
  public String password;
}
