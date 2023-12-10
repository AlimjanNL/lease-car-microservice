package nl.alimjan.car;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import nl.alimjan.car.dto.CarDTO;
import nl.alimjan.car.dto.CarDTOMapper;
import nl.alimjan.car.dto.CarRegistrationRequest;
import nl.alimjan.car.dto.CarUpdateRequest;
import nl.alimjan.utility.exception.RequestValidationException;
import nl.alimjan.utility.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CarService {

  private final CarDao carDao;
  private final CarDTOMapper carDTOMapper;

  public CarService(CarDao carDao, CarDTOMapper carDTOMapper) {
    this.carDao = carDao;
    this.carDTOMapper = carDTOMapper;
  }

  boolean isCarTableEmpty() {
    return carDao.countAllCars() == 0;
  }

  List<CarDTO> getAllCar() {
    return carDao.selectAllCar().stream()
        .map(carDTOMapper)
        .collect(Collectors.toList());
  }

  CarDTO getCarById(Long carId) {
    return carDao.selectCarById(carId).map(carDTOMapper).orElseThrow(
        () -> new ResourceNotFoundException(
            String.format("car with carId [%s] not found.", carId)));
  }

  boolean isExistsCar(Car car) {
    return carDao.isExistsCar(car);
  }

  CarDTO addCar(CarRegistrationRequest carRegistrationRequest) {

    Car car = this.getCarFromCarRegistrationRequest(carRegistrationRequest);

    if (carDao.isExistsCar(car)) {
      throw new ResourceNotFoundException("same car exists.");
    }

    Car addedCar = carDao.insertCar(car);

    return (addedCar != null) ? carDTOMapper.apply(addedCar) : null;
  }

  private Car getCarFromCarRegistrationRequest(CarRegistrationRequest carRegistrationRequest) {
    Car car = new Car();
    car.setMake(carRegistrationRequest.getMake());
    car.setModel(carRegistrationRequest.getModel());
    car.setVersion(carRegistrationRequest.getVersion());
    car.setDoor(carRegistrationRequest.getDoor());
    car.setGrossPrice(carRegistrationRequest.getGrossPrice());
    car.setNettPrice(carRegistrationRequest.getNettPrice());
    car.setHorsepower(carRegistrationRequest.getHorsepower());

    return car;
  }

  CarDTO addCar(Car car) {
    if (!carDao.isExistsCar(car)) {
      Car addedCar = carDao.insertCar(car);

      return (addedCar != null) ? carDTOMapper.apply(addedCar) : null;
    }
    return null;
  }

  void deleteCar(Long id) {
    carDao.deleteCar(id);
  }

  CarDTO updateCar(Long id, CarUpdateRequest carUpdateRequest) {
    Car car = carDao.selectCarById(id).orElseThrow(
        () -> new ResourceNotFoundException(
            String.format("car with carId [%s] not found.", id)));

    boolean change = false;

    if (carUpdateRequest.getMake() != null && !carUpdateRequest.getMake()
        .equals(car.getMake())) {
      car.setMake(carUpdateRequest.getMake());
      change = true;
    }

    if (carUpdateRequest.getModel() != null && !carUpdateRequest.getModel()
        .equals(car.getModel())) {
      car.setModel(carUpdateRequest.getModel());
      change = true;
    }

    if (carUpdateRequest.getVersion() != null && !carUpdateRequest.getVersion()
        .equals(car.getVersion())) {
      car.setVersion(carUpdateRequest.getVersion());
      change = true;
    }

    if (carUpdateRequest.getDoor() != 0
        && carUpdateRequest.getDoor() != car.getDoor()) {
      car.setDoor(carUpdateRequest.getDoor());
      change = true;
    }

    if (carUpdateRequest.getGrossPrice() != null
        && car.getGrossPrice().compareTo(carUpdateRequest.getGrossPrice()) != 0) {
      car.setGrossPrice(carUpdateRequest.getGrossPrice());
      change = true;
    }

    if (carUpdateRequest.getNettPrice() != null
        && car.getNettPrice().compareTo(carUpdateRequest.getNettPrice()) != 0) {
      car.setNettPrice(carUpdateRequest.getNettPrice());
      change = true;
    }

    if (carUpdateRequest.getHorsepower() != 0
        && carUpdateRequest.getHorsepower() != car.getHorsepower()) {
      car.setHorsepower(carUpdateRequest.getHorsepower());
      change = true;
    }

    if (!change) {
      throw new RequestValidationException("no data changes found.");
    }

    Car updatedCar = carDao.updateCar(car);

    return (updatedCar != null) ? carDTOMapper.apply(updatedCar) : null;
  }

  public double calculateLeaserate(double mileage, int duration, double interestRate,
      double nettPrice) {
    double leaseratePart1 = ((mileage / 12) * duration) / nettPrice;
    double leaseratePart2 = ((interestRate / 100) * nettPrice) / 12;
    double leaserate = leaseratePart1 + leaseratePart2;

    return BigDecimal.valueOf(leaserate).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
