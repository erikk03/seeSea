package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import java.util.List;

public interface FiltersService {
  AvailableFiltersDTO getAvailableFilters();

  void persistFilters(FiltersDTO filtersDTO);

  boolean compliesWithUserFilters(JsonNode jsonNode, Long userId);

  List<VesselHistoryData> getVesselHistoryDataFiltered(List<VesselType> vesselTypes, List<VesselStatus> vesselStatuses);
}
