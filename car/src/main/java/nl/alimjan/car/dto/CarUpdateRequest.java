package nl.alimjan.car.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarUpdateRequest {

  public String make;
  public String model;
  public String version;
  public int door;
  public BigDecimal grossPrice;
  public BigDecimal nettPrice;
  public int horsepower;
}
