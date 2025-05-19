package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import java.util.List;
import java.util.Set;

public interface FiltersService {
  AvailableFiltersDTO getAvailableFilters();

  void persistFilters(FiltersDTO filtersDTO);

  boolean compliesWithUserFilters(JsonNode jsonNode, Long userId);

  List<VesselHistoryData> getVesselHistoryDataFiltered(Filters filters, List<String> mmsisFromFleet);
}
