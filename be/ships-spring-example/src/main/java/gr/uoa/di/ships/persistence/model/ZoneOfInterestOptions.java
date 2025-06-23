package gr.uoa.di.ships.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "zone_of_interest_options")
public class ZoneOfInterestOptions {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "max_speed")
  private Float maxSpeed;

  @Column(name = "enters_zone")
  private boolean entersZone;

  @Column(name = "exits_zone")
  private boolean exitsZone;

  @Column(name = "collision_monitoring")
  private boolean collisionMonitoring;

  @OneToOne(mappedBy = "zoneOfInterestOptions")
  private RegisteredUser registeredUser;
}
