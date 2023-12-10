package nl.alimjan.car;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends JpaRepository<Car, Long> {

  boolean existsByMakeAndModelAndVersionAndDoorAndGrossPriceAndNettPriceAndHorsepower(
      String make, String model, String version, int door, BigDecimal grossPrice,
      BigDecimal nettPrice, int horsepower
  );

  @Query("SELECT COUNT(c) FROM Car c")
  Long countAllCars();
}
