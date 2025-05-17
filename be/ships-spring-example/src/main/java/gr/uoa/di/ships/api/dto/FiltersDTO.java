package gr.uoa.di.ships.api.dto;

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
  private Long registeredUserId;
  @NonNull
  private String filterFrom;
  private Long vesselTypeId;
  private Long vesselStatusId;
}
