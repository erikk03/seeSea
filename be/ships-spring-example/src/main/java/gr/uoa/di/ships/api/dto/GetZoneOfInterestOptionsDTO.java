package gr.uoa.di.ships.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetZoneOfInterestOptionsDTO {
  private Long id;
  private Float maxSpeed;
  private boolean entersZone;
  private boolean exitsZone;
  private boolean collisionMonitoring;
}
