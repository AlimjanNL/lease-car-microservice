package nl.alimjan.car.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarDTO {

  public String make;
  public String model;
  public String version;
  public int door;
  public BigDecimal grossPrice;
  public BigDecimal nettPrice;
  public int horsepower;
}
