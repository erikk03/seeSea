package gr.uoa.di.ships.services.interfaces.vessel;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.util.List;
import java.util.Optional;

public interface VesselHistoryDataService {
  void saveVesselHistoryData(List<JsonNode> vesselHistoryDataList);

  List<VesselHistoryDataDTO> setFiltersAndGetMap(FiltersDTO filtersDTO);

  List<VesselHistoryDataDTO> getMap();

  void deleteOldVesselHistoryData();

  Optional<VesselHistoryData> getLastVesselHistoryData(String mmsi);

  List<VesselHistoryDataDTO> getVesselHistoryForTwelveHours(String mmsi);
}
