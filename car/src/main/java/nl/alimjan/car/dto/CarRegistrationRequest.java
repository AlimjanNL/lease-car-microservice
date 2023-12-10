package nl.alimjan.car.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarRegistrationRequest {

  public String make;
  public String model;
  public String version;
  public int door;
  public BigDecimal grossPrice;
  public BigDecimal nettPrice;
  public int horsepower;
}
