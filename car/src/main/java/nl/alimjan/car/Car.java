package nl.alimjan.car;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "car")
public class Car {

  @Id
  @SequenceGenerator(name = "car_id_seq", sequenceName = "car_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_id_seq")
  @Setter(AccessLevel.NONE)
  private Long id;
  @Column(nullable = false)
  private String make;
  @Column(nullable = false)
  private String model;
  @Column(nullable = false)
  private String version;
  @Column(nullable = false)
  private int door;
  @Column(name = "gross_price", nullable = false)
  private BigDecimal grossPrice;
  @Column(name = "nett_price", nullable = false)
  private BigDecimal nettPrice;
  @Column(nullable = false)
  private int horsepower;
}
