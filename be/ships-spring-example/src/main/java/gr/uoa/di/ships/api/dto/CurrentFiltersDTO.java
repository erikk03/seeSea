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

public class CurrentFiltersDTO {
  private String filterFrom;
  private List<Long> vesselTypeIds;
  private List<Long> vesselStatusIds;
}
