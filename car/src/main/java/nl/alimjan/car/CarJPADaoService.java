package nl.alimjan.car;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("jpa")
public class CarJPADaoService implements CarDao {

  private final CarRepository carRepository;

  public CarJPADaoService(CarRepository carRepository) {
    this.carRepository = carRepository;
  }

  public Long countAllCars() {
    return carRepository.countAllCars();
  }

  @Override
  public List<Car> selectAllCar() {
    return carRepository.findAll();
  }

  @Override
  public Optional<Car> selectCarById(Long id) {
    return carRepository.findById(id);
  }

  @Override
  public boolean isExistsCar(Car car) {
    return carRepository.existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
        car.getMake(),
        car.getModel(),
        car.getVersion(),
        car.getDoor(),
        car.getGrossPrice(),
        car.getNettPrice(),
        car.getHorsepower());
  }

  @Override
  public Car insertCar(Car car) {
    return carRepository.save(car);
  }

  @Override
  public void deleteCar(Long id) {
    carRepository.deleteById(id);
  }

  @Override
  public Car updateCar(Car car) {
    return carRepository.save(car);
  }
}
