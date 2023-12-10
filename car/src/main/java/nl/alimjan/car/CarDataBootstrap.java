package nl.alimjan.car;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CarDataBootstrap implements CommandLineRunner {

  private final ResourceLoader resourceLoader;

  public CarDataBootstrap(ResourceLoader resourceLoader, CarService carService) {
    this.resourceLoader = resourceLoader;
    this.carService = carService;
  }

  private final CarService carService;

  @Override
  public void run(String... args) throws Exception {
    if (carService.isCarTableEmpty()) {
      try {
        ClassPathResource resource = new ClassPathResource("test-car-data.csv");
        FileReader fileReader = new FileReader(resource.getFile());
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader csvReader = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();

        List<String[]> allData = csvReader.readAll();

        boolean isFirstRow = true;

        for (String[] row : allData) {
          if (isFirstRow) {
            isFirstRow = false;
            continue;
          }

          String make = row[0];
          String model = row[1];
          String version = row[2];
          String doors = row[3];
          String grossPrice = row[4];
          String nettPrice = row[5];
          String horsepower = row[6];

//          System.out.println("Make: " + make + ", Model: " + model + ", Version: " + version +
//              ", Doors: " + doors + ", Gross Price: " + grossPrice +
//              ", Nett Price: " + nettPrice + ", HP: " + horsepower);

          Car car = new Car();
          car.setMake(make);
          car.setModel(model);
          car.setVersion(version);
          car.setDoor(Integer.parseInt(doors));
          try {
            BigDecimal _grossPrice = new BigDecimal(grossPrice.replace(",", "."));
            BigDecimal _nettPrice = new BigDecimal(nettPrice.replace(",", "."));
            car.setGrossPrice(_grossPrice);
            car.setNettPrice(_nettPrice);
          } catch (NumberFormatException e) {
            log.warn("Invalid number format");
          }

          car.setHorsepower(Integer.parseInt(horsepower));

          carService.addCar(car);
        }
      } catch (Exception e) {
        log.warn(e.getMessage());
      }
    }
  }
}
