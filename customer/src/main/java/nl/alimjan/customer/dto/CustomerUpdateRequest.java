package nl.alimjan.customer.dto;

import lombok.Data;

@Data
public class CustomerUpdateRequest {

  public String name;
  public String email;
  public String street;
  public String housenumber;
  public String zipcode;
  public String place;
  public int phonenumber;
}
