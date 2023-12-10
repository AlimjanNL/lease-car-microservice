package nl.alimjan.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import nl.alimjan.car.dto.CarDTO;
import nl.alimjan.car.dto.CarDTOMapper;
import nl.alimjan.car.dto.CarRegistrationRequest;
import nl.alimjan.car.dto.CarUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CarServiceTest {

  private CarService carService;
  @Mock
  private CarDao carDao;
  AutoCloseable autoCloseable;
  private final CarDTOMapper carDTOMapper = new CarDTOMapper();

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    carService = new CarService(carDao, carDTOMapper);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  void isCarTableEmpty() {

    when(carDao.countAllCars()).thenReturn(0L);

    boolean result = carService.isCarTableEmpty();

    verify(carDao).countAllCars();

    assertThat(result).isTrue();
  }

  @Test
  void selectAllCar() {
    carService.getAllCar();

    verify(carDao).selectAllCar();
  }

  @Test
  void selectCarById() {
    // Given
    Car car = this.getTestCar();
    when(carDao.selectCarById(car.getId())).thenReturn(Optional.of(car));

    CarDTO carDTO = carDTOMapper.apply(car);

    // When
    CarDTO testCustomer = carService.getCarById(car.getId());

    // Assert
    assertThat(testCustomer).isEqualTo(carDTO);
  }

  @Test
  void isExistsCar() {

    Car testCar = this.getTestCar();

    carService.isExistsCar(testCar);

    verify(carDao).isExistsCar(testCar);
  }

  @Test
  void addCarByCarRegistrationRequest() {
    // Given
    Car testCar = this.getTestCar();

    when(carDao.isExistsCar(testCar)).thenReturn(false);

    CarRegistrationRequest request = this.getCarRegistrationRequest();

    // When
    carService.addCar(request);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).insertCar(carArgumentCaptor.capture());

    Car capturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(capturedCar.getId()).isNull();
    assertThat(capturedCar.getMake()).isEqualTo(request.getMake());
    assertThat(capturedCar.getModel()).isEqualTo(request.getModel());
    assertThat(capturedCar.getVersion()).isEqualTo(request.getVersion());
    assertThat(capturedCar.getDoor()).isEqualTo(request.getDoor());
    assertThat(capturedCar.getGrossPrice()).isEqualTo(request.getGrossPrice());
    assertThat(capturedCar.getNettPrice()).isEqualTo(request.getNettPrice());
    assertThat(capturedCar.getHorsepower()).isEqualTo(request.getHorsepower());
  }

  @Test
  void addCarByEntity() {
    // Given
    Car testCar = this.getTestCar();

    when(carDao.isExistsCar(testCar)).thenReturn(false);

    // When
    carService.addCar(testCar);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).insertCar(carArgumentCaptor.capture());

    Car capturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(capturedCar.getId()).isNull();
    assertThat(capturedCar.getMake()).isEqualTo(testCar.getMake());
    assertThat(capturedCar.getModel()).isEqualTo(testCar.getModel());
    assertThat(capturedCar.getVersion()).isEqualTo(testCar.getVersion());
    assertThat(capturedCar.getDoor()).isEqualTo(testCar.getDoor());
    assertThat(capturedCar.getGrossPrice()).isEqualTo(testCar.getGrossPrice());
    assertThat(capturedCar.getNettPrice()).isEqualTo(testCar.getNettPrice());
    assertThat(capturedCar.getHorsepower()).isEqualTo(testCar.getHorsepower());
  }

  @Test
  void deleteCar() {
    // Given
    Long id = 1L;

    // When
    carService.deleteCar(id);

    // Then
    verify(carDao).deleteCar(id);
  }

  @Test
  void updateCarAllFields() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    String newEmail = "alex.malex@test.com";

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setMake("wv");
    updateRequest.setModel("wv1");
    updateRequest.setVersion("2023");
    updateRequest.setDoor(4);
    updateRequest.setGrossPrice(new BigDecimal("1111.00"));
    updateRequest.setNettPrice(new BigDecimal("2222.00"));
    updateRequest.setHorsepower(200);

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(updateRequest.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(updateRequest.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(updateRequest.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(updateRequest.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(updateRequest.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(updateRequest.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(updateRequest.getHorsepower());
  }

  @Test
  void updateCarMake() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setMake("wv2");

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(updateRequest.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarModel() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setModel("model2");

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(updateRequest.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarVersion() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setVersion("version2");

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(updateRequest.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarDoor() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setDoor(2);

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(updateRequest.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarGrossPrice() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setGrossPrice(new BigDecimal("9999.99"));

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(updateRequest.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarNettPrice() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setNettPrice(new BigDecimal("1111.11"));

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(updateRequest.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(car.getHorsepower());
  }

  @Test
  void updateCarHorsepower() {
    // Given
    Long id = 1L;
    Car car = this.getTestCar();

    when(carDao.selectCarById(id)).thenReturn(Optional.of(car));

    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setHorsepower(100);

    when(carDao.isExistsCar(car)).thenReturn(false);

    // When
    carService.updateCar(id, updateRequest);

    // Then
    ArgumentCaptor<Car> carArgumentCaptor = ArgumentCaptor.forClass(Car.class);

    Mockito.verify(carDao).updateCar(carArgumentCaptor.capture());

    Car cpturedCar = carArgumentCaptor.getValue();

    // Assert
    assertThat(cpturedCar.getMake()).isEqualTo(car.getMake());
    assertThat(cpturedCar.getModel()).isEqualTo(car.getModel());
    assertThat(cpturedCar.getVersion()).isEqualTo(car.getVersion());
    assertThat(cpturedCar.getDoor()).isEqualTo(car.getDoor());
    assertThat(cpturedCar.getGrossPrice()).isEqualTo(car.getGrossPrice());
    assertThat(cpturedCar.getNettPrice()).isEqualTo(car.getNettPrice());
    assertThat(cpturedCar.getHorsepower()).isEqualTo(updateRequest.getHorsepower());
  }

  @Test
  void calculateLeaserate(){
    // Given
    double mileage = 45000.0;
    int duration = 60;
    double interestRate = 4.5;
    double nettPrice = 63000.0;

    // When
    double leaserate = carService.calculateLeaserate(mileage, duration, interestRate, nettPrice);

    // Then
    double expectedLeaserate = ((mileage / 12) * duration) / nettPrice
        + ((interestRate / 100) * nettPrice) / 12;

    // Assert
    Assertions.assertEquals(expectedLeaserate, leaserate, 0.01);
  }

  private Car getTestCar() {
    Car testCar = new Car();
    testCar.setMake("Toyota");
    testCar.setModel("Camry");
    testCar.setVersion("2023");
    testCar.setDoor(4);
    testCar.setGrossPrice(new BigDecimal("25000.00"));
    testCar.setNettPrice(new BigDecimal("22000.00"));
    testCar.setHorsepower(200);

    return testCar;
  }

  private CarRegistrationRequest getCarRegistrationRequest() {
    CarRegistrationRequest request = new CarRegistrationRequest();
    request.setMake("Toyota");
    request.setModel("Camry");
    request.setVersion("2023");
    request.setDoor(4);
    request.setGrossPrice(new BigDecimal("25000.00"));
    request.setNettPrice(new BigDecimal("22000.00"));
    request.setHorsepower(200);

    return request;
  }
}