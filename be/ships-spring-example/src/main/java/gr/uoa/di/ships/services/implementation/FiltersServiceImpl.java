package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import gr.uoa.di.ships.persistence.model.enums.FilterFromEnum;
import gr.uoa.di.ships.persistence.repository.FiltersRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.VesselService;
import gr.uoa.di.ships.services.interfaces.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;

  public FiltersServiceImpl(VesselTypeService vesselTypeService,
                            VesselStatusService vesselStatusService,
                            FiltersRepository filtersRepository,
                            RegisteredUserService registeredUserService,
                            VesselService vesselService,
                            SeeSeaUserDetailsService seeSeaUserDetailsService) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
    this.filtersRepository = filtersRepository;
    this.registeredUserService = registeredUserService;
    this.vesselService = vesselService;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
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
    RegisteredUser registeredUser = seeSeaUserDetailsService.getUserDetails();
    Filters filters = Optional.ofNullable(filtersRepository.findByRegisteredUserId(registeredUser.getId()))
        .orElseGet(() -> Filters.builder()
                       .registeredUser(registeredUser)
                       .build());
    filters.setFilterFrom(filtersDTO.getFilterFrom());
    filters.setVesselTypes(vesselTypeService.findVesselTypesByIds(filtersDTO.getVesselTypeIds()));
    filters.setVesselStatuses(vesselStatusService.getVesselStatusesByIds(filtersDTO.getVesselStatusIds()));
    filtersRepository.save(filters);
    registeredUser.setFilters(filters);
    registeredUserService.saveRegisteredUser(registeredUser);
  }

  @Override
  public boolean compliesWithUserFilters(JsonNode jsonNode, Long userId) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(userId);
    Filters filters = registeredUser.getFilters();
    if (Objects.isNull(filters)) {
      return true;
    }
    String mmsi = jsonNode.get("mmsi").asText();
    return compliesWithFilterFrom(filters.getFilterFrom(), mmsi, registeredUser)
        && compliesWithVesselTypes(filters.getVesselTypes(), mmsi)
        && compliesWithVesselStatuses(filters.getVesselStatuses(), jsonNode.get("status").asLong());
  }

  @Override
  public List<VesselHistoryData> getVesselHistoryDataFiltered(Filters filters, List<String> mmsisFromFleet) {
    String filterFrom = filters.getFilterFrom();
    List<VesselType> vesselTypes = Optional.of(filters)
        .map(Filters::getVesselTypes)
        .filter(list -> !list.isEmpty())
        .orElseGet(vesselTypeService::findAllVesselTypes);
    List<VesselStatus> vesselStatuses = Optional.of(filters)
        .map(Filters::getVesselStatuses)
        .filter(list -> !list.isEmpty())
        .orElseGet(vesselStatusService::findAllVesselStatuses);
    List<Long> vesselTypeIds = vesselTypes.stream().map(VesselType::getId).toList();
    List<Long> vesselStatusIds = vesselStatuses.stream().map(VesselStatus::getId).toList();
    if (filterFrom.equals(FilterFromEnum.MY_FLEET.getDescription())) {
      return filtersRepository.getVesselHistoryDataFilteredByFleet(vesselTypeIds, vesselStatusIds, mmsisFromFleet);
    }
    return filtersRepository.getVesselHistoryDataFiltered(vesselTypeIds, vesselStatusIds);
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