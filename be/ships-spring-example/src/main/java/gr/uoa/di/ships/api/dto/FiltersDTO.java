package gr.uoa.di.ships.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiltersDTO {
  @NonNull
  private String filterFrom;
  private List<Long> vesselTypeIds;
  private List<Long> vesselStatusIds;
}
