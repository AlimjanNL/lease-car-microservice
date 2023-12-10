package nl.alimjan.car.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaseRequest {
  private double mileage;
  private int duration;
  private double interestRate;
  private double nettPrice;
}
