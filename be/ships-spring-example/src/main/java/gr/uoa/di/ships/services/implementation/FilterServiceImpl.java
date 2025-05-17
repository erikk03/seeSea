package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.persistence.model.enums.FIlterFromEnum;
import gr.uoa.di.ships.services.interfaces.FilterService;
import gr.uoa.di.ships.services.interfaces.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class FilterServiceImpl implements FilterService {

  private final VesselTypeService vesselTypeService;
  private final VesselStatusService vesselStatusService;

  public FilterServiceImpl(VesselTypeService vesselTypeService, VesselStatusService vesselStatusService) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
  }

  @Override
  public AvailableFiltersDTO getAvailableFilters() {
    return AvailableFiltersDTO.builder()
        .filterFrom(List.of(FIlterFromEnum.ALL.getDescription(), FIlterFromEnum.MY_FLEET.getDescription()))
        .vesselTypes(vesselTypeService.findAllVesselTypes())
        .vesselStatuses(vesselStatusService.findAllVesselStatuses())
        .build();
  }
}