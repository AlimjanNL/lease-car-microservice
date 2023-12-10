package nl.alimjan.car;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CarRepositoryTest {

  @Autowired
  private CarRepository carRepository;

  @BeforeEach
  void beforeEach() {
    carRepository.deleteAll();
    Car car = new Car();
    car.setMake("Lexus");
    car.setModel("IS220d");
    car.setVersion("Sport");
    car.setDoor(4);
    car.setGrossPrice(new BigDecimal("44285"));
    car.setNettPrice(new BigDecimal("28488.66"));
    car.setHorsepower(177);

    carRepository.save(car);
  }

  @AfterEach
  public void afterEach() {
    carRepository.deleteAll();
  }

  @Test
  public void existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower() {

    boolean isExist = carRepository.existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
        "Lexus",
        "IS220d",
        "Sport",
        4,
        new BigDecimal("44285"),
        new BigDecimal("28488.66"),
        177
    );

    assertThat(isExist).isTrue();
  }

  @Test
  public void existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepowerWhenMakeDifferent() {

    boolean isExist = carRepository.existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
        "Volkswagen",
        "IS220d",
        "Sport",
        4,
        new BigDecimal("44285"),
        new BigDecimal("28488.66"),
        177
    );

    assertThat(isExist).isFalse();
  }

  @Test
  public void countAllCars() {

    Long count = carRepository.countAllCars();

    int countAsInt = count.intValue();

    assertThat(countAsInt).isEqualTo(1);
  }

  @Test
  public void countAllCarsWhenAddNewCar() {

    Car car = new Car();
    car.setMake("NewLexus");
    car.setModel("1111");
    car.setVersion("Sport");
    car.setDoor(2);
    car.setGrossPrice(new BigDecimal("44285"));
    car.setNettPrice(new BigDecimal("28488.66"));
    car.setHorsepower(177);

    carRepository.save(car);

    Long count = carRepository.countAllCars();

    int countAsInt = count.intValue();

    assertThat(countAsInt).isEqualTo(2);
  }
}