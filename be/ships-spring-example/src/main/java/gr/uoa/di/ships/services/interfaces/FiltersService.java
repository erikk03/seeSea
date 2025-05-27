package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.CurrentFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.util.List;

public interface FiltersService {
  AvailableFiltersDTO getAvailableFilters();

  void persistFilters(FiltersDTO filtersDTO);

  boolean compliesWithUserFilters(JsonNode jsonNode, Long userId);

  List<VesselHistoryData> getVesselHistoryDataFiltered();

  CurrentFiltersDTO getCurrentFilters();
}

