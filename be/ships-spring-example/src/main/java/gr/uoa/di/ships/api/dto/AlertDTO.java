package gr.uoa.di.ships.api.dto;

import java.util.List;
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
public class AlertDTO {
  private Long userId;
  private String vesselMmsi;
  private List<String> alertDescriptions;
}
