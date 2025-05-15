package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import java.util.List;

public interface VesselHistoryDataService {
  void saveVesselHistoryData(List<JsonNode> vesselHistoryDataList);

  List<VesselHistoryDataDTO> getMap();
}
