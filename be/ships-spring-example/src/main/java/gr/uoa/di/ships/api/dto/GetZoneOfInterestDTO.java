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
public class GetZoneOfInterestDTO {
  private Long id;
  private Double radius;
  private Double centerPointLatitude;
  private Double centerPointLongitude;
}
