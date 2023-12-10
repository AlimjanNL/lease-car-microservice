package nl.alimjan.car.dto;

import java.util.function.Function;
import nl.alimjan.car.Car;
import org.springframework.stereotype.Service;

@Service
public class CarDTOMapper implements Function<Car, CarDTO> {

  @Override
  public CarDTO apply(Car car) {
    return new CarDTO(
        car.getMake(),
        car.getModel(),
        car.getVersion(),
        car.getDoor(),
        car.getGrossPrice(),
        car.getNettPrice(),
        car.getHorsepower());
  }
}
