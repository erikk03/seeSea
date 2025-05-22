package gr.uoa.di.ships.api.dto;

import jakarta.validation.constraints.NotNull;
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
public class VesselHistoryDataDTO {
  @NotNull
  private String mmsi;
  private String vesselType;
  private String status;
  private Float turn;
  private Float speed;
  private Float course;
  private Integer heading;
  private Double lon;
  private Double lat;
  private Long timestamp;
}
