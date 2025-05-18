package gr.uoa.di.ships.api.dto;

import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
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
public class AvailableFiltersDTO {
  List<String> filterFrom;
  List<VesselType> vesselTypes;
  List<VesselStatus> vesselStatuses;
}
