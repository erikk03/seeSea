package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import gr.uoa.di.ships.persistence.model.enums.FilterFromEnum;
import gr.uoa.di.ships.persistence.repository.FiltersRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.VesselService;
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
  private final VesselService vesselService;

  public FiltersServiceImpl(VesselTypeService vesselTypeService,
                            VesselStatusService vesselStatusService,
                            FiltersRepository filtersRepository,
                            RegisteredUserService registeredUserService,
                            VesselService vesselService) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
    this.filtersRepository = filtersRepository;
    this.registeredUserService = registeredUserService;
    this.vesselService = vesselService;
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
    List<VesselType> vesselTypes = vesselTypeService.findVesselTypesByIds(filtersDTO.getVesselTypeIds());
    List<VesselStatus> vesselStatuses = vesselStatusService.findVesselStatusesByIds(filtersDTO.getVesselStatusIds());
    if (!FilterFromEnum.isValidFilterFrom(filtersDTO.getFilterFrom())) {
      throw new IllegalArgumentException("Invalid filterFrom value: " + filtersDTO.getFilterFrom());
    }
    Filters filters = Optional.ofNullable(
        filtersRepository.findByRegisteredUserId(filtersDTO.getRegisteredUserId())
    ).orElseGet(() -> Filters.builder()
        .registeredUser(registeredUserService.getRegisteredUserById(filtersDTO.getRegisteredUserId()))
        .filterFrom(filtersDTO.getFilterFrom())
        .vesselTypes(vesselTypes)
        .vesselStatuses(vesselStatuses)
        .build()
    );
    if (Objects.nonNull(filters.getId())) {
      filters.setFilterFrom(filtersDTO.getFilterFrom());
      filters.setVesselTypes(vesselTypes);
      filters.setVesselStatuses(vesselStatuses);
    }
    filtersRepository.save(filters);
  }

  @Override
  public boolean compliesWithUserFilters(JsonNode jsonNode, Long userId) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(userId);
    Filters filters = filtersRepository.findByRegisteredUserId(userId);
    if (Objects.isNull(filters)) {
      return true;
    }
    String mmsi = jsonNode.get("mmsi").asText();
    return compliesWithFilterFrom(filters.getFilterFrom(), mmsi, registeredUser)
        && compliesWithVesselTypes(filters.getVesselTypes(), mmsi)
        && compliesWithVesselStatuses(filters.getVesselStatuses(), jsonNode.get("status").asLong());
  }

  private boolean compliesWithFilterFrom(String filterFrom, String mmsi, RegisteredUser registeredUser) {
    if (filterFrom.equals(FilterFromEnum.MY_FLEET.getDescription())) {
      return registeredUser.getVessels()
          .stream()
          .map(Vessel::getMmsi)
          .anyMatch(vesselMmsi -> vesselMmsi.equals(mmsi));
    }
    return true;
  }

  private boolean compliesWithVesselTypes(List<VesselType> filterVesselTypes, String mmsi) {
    if (filterVesselTypes.isEmpty()) {
      return true;
    }
    return vesselService.getVesselByMMSI(mmsi)
        .map(Vessel::getVesselType)
        .map(filterVesselTypes::contains)
        .orElse(false);
  }

  private boolean compliesWithVesselStatuses(List<VesselStatus> filterVesselStatuses, Long statusId) {
    return filterVesselStatuses.isEmpty()
        || filterVesselStatuses.stream().map(VesselStatus::getId).anyMatch(id -> id.equals(statusId));
  }
}