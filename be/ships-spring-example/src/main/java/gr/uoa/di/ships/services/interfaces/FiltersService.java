package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;

public interface FiltersService {
  AvailableFiltersDTO getAvailableFilters();

  void persistFilters(FiltersDTO filtersDTO);
}
