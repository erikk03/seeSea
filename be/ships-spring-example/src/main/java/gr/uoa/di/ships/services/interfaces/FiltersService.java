package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;

public interface FiltersService {
  AvailableFiltersDTO getAvailableFilters();

  void persistFilters(FiltersDTO filtersDTO);

  boolean compliesWithUserFilters(JsonNode jsonNode, Long userId);
}
