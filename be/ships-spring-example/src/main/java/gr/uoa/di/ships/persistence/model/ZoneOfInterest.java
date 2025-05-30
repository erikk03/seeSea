package gr.uoa.di.ships.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "zone_of_interest")
public class ZoneOfInterest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "radius", nullable = false)
  private Double radius;

  @Column(name = "center_point_latitude", nullable = false)
  private Double centerPointLatitude;

  @Column(name = "center_point_longitude", nullable = false)
  private Double centerPointLongitude;

  @OneToOne(mappedBy = "zoneOfInterest")
  private RegisteredUser registeredUser;

  @Column(name = "datetime_created")
  private LocalDateTime datetimeCreated;
}
