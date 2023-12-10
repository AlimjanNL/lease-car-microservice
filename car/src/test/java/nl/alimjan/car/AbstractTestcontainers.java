package nl.alimjan.car;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestcontainers {

  @BeforeAll
  static void beforeAll() {
    Flyway flyway = Flyway.configure().dataSource(
        postgreSQLContainer.getJdbcUrl(),
        postgreSQLContainer.getUsername(),
        postgreSQLContainer.getPassword()
    ).load();

    flyway.migrate();
  }

  @Container
  protected static PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:latest")
          .withDatabaseName("car")
          .withUsername("postgres")
          .withPassword("password");

}
