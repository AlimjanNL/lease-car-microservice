package nl.alimjan.car;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import nl.alimjan.car.dto.CarDTO;
import nl.alimjan.car.dto.CarRegistrationRequest;
import nl.alimjan.car.dto.CarUpdateRequest;
import nl.alimjan.car.dto.LeaseRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CarControllerTest {

  @Mock
  private CarService carService;
  @InjectMocks
  private CarController carController;
  private final ObjectMapper objectMapper = new ObjectMapper();
  AutoCloseable autoCloseable;

  @BeforeEach
  void beforeEach() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    carController = new CarController(carService);
  }

  @AfterEach
  void afterEach() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getCars() throws Exception {
    List<CarDTO> carList = Arrays.asList(
        new CarDTO("Toyota", "Camry", "2023", 2, new BigDecimal("12345.00"),
            new BigDecimal("54321.00"),
            100),
        new CarDTO("WV", "Rock", "2024", 2, new BigDecimal("54321.00"), new BigDecimal("12345.00"),
            200)
    );

    when(carService.getAllCar()).thenReturn(carList);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(get("/api/v1/cars"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$[0].make").value("Toyota"))
        .andExpect(jsonPath("$[1].make").value("WV"));

    verify(carService, times(1)).getAllCar();
    verifyNoMoreInteractions(carService);
  }

  @Test
  void getCar() throws Exception {
    CarDTO carDTO = new CarDTO("Toyota", "Camry", "2023", 2, new BigDecimal("12345.00"),
        new BigDecimal("54321.00"),
        100);
    Long carId = 1L;

    when(carService.getCarById(carId)).thenReturn(carDTO);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(get("/api/v1/cars/{carId}", carId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.make").value("Toyota"));

    verify(carService, times(1)).getCarById(carId);
    verifyNoMoreInteractions(carService);
  }

  @Test
  void registerCar() throws Exception {
    CarRegistrationRequest request = new CarRegistrationRequest();
    request.setMake("Toyota");
    request.setModel("Camry");
    request.setVersion("2023");
    request.setDoor(4);
    request.setGrossPrice(new BigDecimal("25000.00"));
    request.setNettPrice(new BigDecimal("22000.00"));
    request.setHorsepower(200);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(post("/api/v1/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andExpect(status().isCreated());

    verify(carService, times(1)).addCar(request);
    verifyNoMoreInteractions(carService);
  }

  @Test
  void deleteCar() throws Exception {
    Long carId = 1L;

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(delete("/api/v1/cars/{carId}", carId))
        .andExpect(status().isOk());

    verify(carService, times(1)).deleteCar(carId);
    verifyNoMoreInteractions(carService);
  }

  @Test
  void updateCar() throws Exception {
    Long carId = 1L;
    CarUpdateRequest updateRequest = new CarUpdateRequest();
    updateRequest.setModel("Updated model");

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(put("/api/v1/cars/{carId}", carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(updateRequest)))
        .andExpect(status().isOk());

    verify(carService, times(1)).updateCar(carId, updateRequest);
    verifyNoMoreInteractions(carService);
  }

  @Test
  void calculateLease() throws Exception {
    LeaseRequest leaseRequest = new LeaseRequest();
    leaseRequest.setMileage(45000.0);
    leaseRequest.setDuration(60);
    leaseRequest.setInterestRate(4.5);
    leaseRequest.setNettPrice(63000.0);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(carController).build();

    mockMvc.perform(post("/api/v1/cars/lease-calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(leaseRequest)))
        .andExpect(status().isOk());

    verify(carService, times(1)).calculateLeaserate(
        leaseRequest.getMileage(),
        leaseRequest.getDuration(),
        leaseRequest.getInterestRate(),
        leaseRequest.getNettPrice()
    );
    verifyNoMoreInteractions(carService);
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}