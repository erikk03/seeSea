package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.VesselDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselMapper;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class VesselMapperImpl implements VesselMapper {

  private static final String NOT_DEFINED_DEFAULT_ALSO_USED_BY_AIS_SART_UNDER_TEST = "not defined = default (also used by AIS-SART under test)";
  private final VesselHistoryDataService vesselHistoryDataService;

  public VesselMapperImpl(VesselHistoryDataService vesselHistoryDataService) {
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @Override
  public VesselDTO toVesselDTO(Vessel vessel) {
    return VesselDTO.builder()
        .mmsi(vessel.getMmsi())
        .type(vessel.getVesselType().getName())
        .status(vesselHistoryDataService.getLastVesselHistoryDataForMmsi(vessel.getMmsi())
                    .map(vhd -> vhd.getVesselStatus().getName())
                    .orElse(NOT_DEFINED_DEFAULT_ALSO_USED_BY_AIS_SART_UNDER_TEST))
        .build();
  }
}
