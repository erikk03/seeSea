package gr.uoa.di.ships.api.mapper.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;

public interface VesselHistoryDataMapper {
  VesselHistoryData toVesselHistoryData(JsonNode vesselHistoryDataJsonNode);

  VesselHistoryDataDTO toVesselHistoryDataDTO(VesselHistoryData vesselHistoryData);
}
