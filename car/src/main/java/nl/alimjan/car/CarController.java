package nl.alimjan.car;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.alimjan.car.dto.CarDTO;
import nl.alimjan.car.dto.CarRegistrationRequest;
import nl.alimjan.car.dto.CarUpdateRequest;
import nl.alimjan.car.dto.LeaseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cars")
@Slf4j
public class CarController {

  private final CarService carService;

  public CarController(CarService carService) {
    this.carService = carService;
  }

  @GetMapping
  public ResponseEntity<List<CarDTO>> getCars() {
    log.info("Get request for all cars");

    List<CarDTO> carDTOList = carService.getAllCar();

    return ResponseEntity.status(HttpStatus.OK).body(carDTOList);
  }

  @GetMapping("/{carId}")
  public ResponseEntity<CarDTO> getCar(@PathVariable Long carId) {
    log.info(String.format("Get request for car: [%s]", carId));

    CarDTO carDTO = carService.getCarById(carId);

    return ResponseEntity.status(HttpStatus.OK).body(carDTO);
  }

  @PostMapping()
  public ResponseEntity<CarDTO> registerCar(
      @RequestBody CarRegistrationRequest carRegistrationRequest) {
    log.info(String.format("Get request for car: [%s] [%s] [%s]", carRegistrationRequest.getMake(),
        carRegistrationRequest.getModel(), carRegistrationRequest.getVersion()));

    CarDTO carDTO = carService.addCar(carRegistrationRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(carDTO);
  }

  @DeleteMapping("/{carId}")
  public ResponseEntity<String> deleteCar(@PathVariable Long carId) {
    log.info(String.format("Delete request for car: [%s]", carId));

    carService.deleteCar(carId);

    return ResponseEntity.ok("Car deleted successfully");
  }

  @PutMapping("/{carId}")
  public ResponseEntity<CarDTO> updateCar(@PathVariable Long carId,
      @RequestBody CarUpdateRequest carUpdateRequest) {
    log.info(String.format("Put request for update car: [%s]", carId));

    CarDTO carDTO = carService.updateCar(carId, carUpdateRequest);

    return ResponseEntity.status(HttpStatus.OK).body(carDTO);
  }

  @PostMapping("/lease-calculate")
  public ResponseEntity<String> calculateLease(@RequestBody LeaseRequest leaseRequest) {
    double mileage = leaseRequest.getMileage();
    int duration = leaseRequest.getDuration();
    double interestRate = leaseRequest.getInterestRate();
    double nettPrice = leaseRequest.getNettPrice();

    double leaseRate = carService.calculateLeaserate(mileage, duration, interestRate, nettPrice);

    log.info("Lease calculation successful for car lease request: {}", leaseRequest);
    return ResponseEntity.ok("Leaserate: € " + leaseRate + " per month");
  }

}
