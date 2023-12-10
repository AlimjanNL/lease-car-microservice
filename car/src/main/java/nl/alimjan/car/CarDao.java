package nl.alimjan.car;

import java.util.List;
import java.util.Optional;

public interface CarDao {

  Long countAllCars();

  List<Car> selectAllCar();

  Optional<Car> selectCarById(Long id);

  boolean isExistsCar(Car car);

  Car insertCar(Car car);

  void deleteCar(Long id);

  Car updateCar(Car car);

}
