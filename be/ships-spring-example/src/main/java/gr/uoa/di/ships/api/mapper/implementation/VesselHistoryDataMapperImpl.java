package gr.uoa.di.ships.api.mapper.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import gr.uoa.di.ships.services.interfaces.VesselService;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class VesselHistoryDataMapperImpl implements VesselHistoryDataMapper {

  private final VesselService vesselService;

  public VesselHistoryDataMapperImpl(VesselService vesselService) {
    this.vesselService = vesselService;
  }

  @Override
  public VesselHistoryData toVesselHistoryData(JsonNode vesselHistoryDataJsonNode) {
    String mmsi = vesselHistoryDataJsonNode.get("mmsi").asText();
    Optional<Vessel> optionalVessel = vesselService.getVesselByMMSI(mmsi);
    Vessel vessel = optionalVessel.orElseGet(() -> vesselService.saveVessel(new Vessel(mmsi)));
    return VesselHistoryData.builder()
        .vessel(vessel)
        .status(vesselHistoryDataJsonNode.get("status").asInt())
        .turn((float) vesselHistoryDataJsonNode.get("turn").asDouble())
        .speed((float) vesselHistoryDataJsonNode.get("speed").asDouble())
        .course((float) vesselHistoryDataJsonNode.get("course").asDouble())
        .heading(vesselHistoryDataJsonNode.get("heading").asInt())
        .longitude(vesselHistoryDataJsonNode.get("lon").asDouble())
        .latitude(vesselHistoryDataJsonNode.get("lat").asDouble())
        .timestamp(vesselHistoryDataJsonNode.get("timestamp").asLong())
        .build();
  }

  @Override
  public VesselHistoryDataDTO toVesselHistoryDataDTO(VesselHistoryData vesselHistoryData) {
    return VesselHistoryDataDTO.builder()
        .mmsi(vesselHistoryData.getVessel().getMmsi())
        .vesselType(
            Objects.nonNull(vesselHistoryData.getVessel().getVesselType())
                ? vesselHistoryData.getVessel().getVesselType().getName()
                : null)
        .status(vesselHistoryData.getStatus())
        .turn(vesselHistoryData.getTurn())
        .speed(vesselHistoryData.getSpeed())
        .course(vesselHistoryData.getCourse())
        .heading(vesselHistoryData.getHeading())
        .longitude(vesselHistoryData.getLongitude())
        .latitude(vesselHistoryData.getLatitude())
        .timestamp(vesselHistoryData.getTimestamp())
        .build();
  }
}
