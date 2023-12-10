package nl.alimjan.car;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CarJPADaoServiceTest {

  private CarJPADaoService carJPADaoService;

  @Mock
  private CarRepository carRepository;

  private AutoCloseable autoCloseable;

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    carJPADaoService = new CarJPADaoService(carRepository);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  public void countAllCars() {
    carJPADaoService.countAllCars();

    Mockito.verify(carRepository).countAllCars();
  }

  @Test
  public void selectAllCar() {
    carJPADaoService.selectAllCar();

    Mockito.verify(carRepository).findAll();
  }

  @Test
  public void selectCarById() {
    Long id = 1L;

    carJPADaoService.selectCarById(id);

    Mockito.verify(carRepository).findById(id);
  }

  @Test
  public void isExistsCar() {

    Car testCar = this.getTestCar();

    when(carRepository.existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
        any(), any(), any(), anyInt(), any(), any(), anyInt()))
        .thenReturn(true);

    boolean result = carJPADaoService.isExistsCar(testCar);

    verify(
        carRepository).existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
        eq(testCar.getMake()), eq(testCar.getModel()), eq(testCar.getVersion()),
        eq(testCar.getDoor()), eq(testCar.getGrossPrice()), eq(testCar.getNettPrice()),
        eq(testCar.getHorsepower()));

    assertThat(result).isTrue();
  }

  @Test
  public void insertCar() {

    Car testCar = this.getTestCar();

    carJPADaoService.insertCar(testCar);

    Mockito.verify(carRepository).save(testCar);
  }

  @Test
  public void deleteCar() {
    Long id = 1L;

    carJPADaoService.deleteCar(id);

    Mockito.verify(carRepository).deleteById(id);
  }

  @Test
  public void updateCar() {
    Car testCar = this.getTestCar();

    carJPADaoService.updateCar(testCar);

    Mockito.verify(carRepository).save(testCar);
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
}