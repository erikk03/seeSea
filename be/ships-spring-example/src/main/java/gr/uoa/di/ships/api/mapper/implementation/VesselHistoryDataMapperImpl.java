package gr.uoa.di.ships.api.mapper.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class VesselHistoryDataMapperImpl implements VesselHistoryDataMapper {

  private static final String UNKNOWN = "unknown";
  private final VesselService vesselService;
  private final VesselStatusService vesselStatusService;
  private final VesselTypeService vesselTypeService;

  public VesselHistoryDataMapperImpl(VesselService vesselService,
                                     VesselStatusService vesselStatusService,
                                     VesselTypeService vesselTypeService) {
    this.vesselService = vesselService;
    this.vesselStatusService = vesselStatusService;
    this.vesselTypeService = vesselTypeService;
  }

  @Override
  public VesselHistoryData toVesselHistoryData(JsonNode vesselHistoryDataJsonNode) {
    String mmsi = vesselHistoryDataJsonNode.get("mmsi").asText();
    return VesselHistoryData.builder()
        .vessel(vesselService.getVesselByMMSI(mmsi)
                    .orElseGet(() -> vesselService.saveVessel(new Vessel(mmsi, vesselTypeService.findVesselTypeByName(UNKNOWN))))
        )
        .vesselStatus(vesselStatusService.getVesselStatusById(vesselHistoryDataJsonNode.get("status").asLong()))
        .turn((float) vesselHistoryDataJsonNode.get("turn").asDouble())
        .speed((float) vesselHistoryDataJsonNode.get("speed").asDouble())
        .course((float) vesselHistoryDataJsonNode.get("course").asDouble())
        .heading(vesselHistoryDataJsonNode.get("heading").asInt())
        .longitude(vesselHistoryDataJsonNode.get("lon").asDouble())
        .latitude(vesselHistoryDataJsonNode.get("lat").asDouble())
        .timestamp(vesselHistoryDataJsonNode.get("timestamp").asLong())
        .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
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
        .status(vesselHistoryData.getVesselStatus().getName())
        .turn(vesselHistoryData.getTurn())
        .speed(vesselHistoryData.getSpeed())
        .course(vesselHistoryData.getCourse())
        .heading(vesselHistoryData.getHeading())
        .lon(vesselHistoryData.getLongitude())
        .lat(vesselHistoryData.getLatitude())
        .timestamp(vesselHistoryData.getTimestamp())
        .build();
  }
}
