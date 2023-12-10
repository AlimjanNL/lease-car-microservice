package nl.alimjan.customer.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

  public String name;
  public String email;
  public String street;
  public String housenumber;
  public String zipcode;
  public String place;
  public int phonenumber;
  List<String> roles;
}
