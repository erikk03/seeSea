package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import gr.uoa.di.ships.persistence.model.enums.FilterFromEnum;
import gr.uoa.di.ships.persistence.repository.FiltersRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class FiltersServiceImpl implements FiltersService {

  private final VesselTypeService vesselTypeService;
  private final VesselStatusService vesselStatusService;
  private final FiltersRepository filtersRepository;
  private final RegisteredUserService registeredUserService;

  public FiltersServiceImpl(VesselTypeService vesselTypeService,
                            VesselStatusService vesselStatusService,
                            FiltersRepository filtersRepository,
                            RegisteredUserService registeredUserService) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
    this.filtersRepository = filtersRepository;
    this.registeredUserService = registeredUserService;
  }

  @Override
  public AvailableFiltersDTO getAvailableFilters() {
    return AvailableFiltersDTO.builder()
        .filterFrom(List.of(FilterFromEnum.ALL.getDescription(), FilterFromEnum.MY_FLEET.getDescription()))
        .vesselTypes(vesselTypeService.findAllVesselTypes())
        .vesselStatuses(vesselStatusService.findAllVesselStatuses())
        .build();
  }

  @Override
  public void persistFilters(FiltersDTO filtersDTO) {
    if (!FilterFromEnum.isValidFilterFrom(filtersDTO.getFilterFrom())) {
      throw new IllegalArgumentException("Invalid filterFrom value: " + filtersDTO.getFilterFrom());
    }
    VesselType vesselType = Objects.nonNull(filtersDTO.getVesselTypeId())
        ? vesselTypeService.findVesselTypeById(filtersDTO.getVesselTypeId())
        : null;
    VesselStatus vesselStatus = Objects.nonNull(filtersDTO.getVesselStatusId())
        ? vesselStatusService.findVesselStatusById(filtersDTO.getVesselStatusId())
        : null;
    Filters filters = Optional.ofNullable(
        filtersRepository.findByRegisteredUserId(filtersDTO.getRegisteredUserId())
    ).orElseGet(() -> Filters.builder()
        .registeredUser(registeredUserService.getRegisteredUserById(filtersDTO.getRegisteredUserId()))
        .filterFrom(filtersDTO.getFilterFrom())
        .vesselType(vesselType)
        .vesselStatus(vesselStatus)
        .build()
    );
    if (Objects.nonNull(filters.getId())) {
      filters.setFilterFrom(filtersDTO.getFilterFrom());
      filters.setVesselType(vesselType);
      filters.setVesselStatus(vesselStatus);
    }
    filtersRepository.save(filters);
  }
}