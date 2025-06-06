package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.CurrentFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselStatusMapper;
import gr.uoa.di.ships.api.mapper.interfaces.VesselTypeMapper;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.enums.FilterFromEnum;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.FiltersRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
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
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;
  private final VesselTypeMapper vesselTypeMapper;
  private final VesselStatusMapper vesselStatusMapper;

  public FiltersServiceImpl(VesselTypeService vesselTypeService,
                            VesselStatusService vesselStatusService,
                            FiltersRepository filtersRepository,
                            RegisteredUserService registeredUserService,
                            VesselService vesselService,
                            SeeSeaUserDetailsService seeSeaUserDetailsService,
                            VesselTypeMapper vesselTypeMapper,
                            VesselStatusMapper vesselStatusMapper) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
    this.filtersRepository = filtersRepository;
    this.registeredUserService = registeredUserService;
    this.vesselService = vesselService;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.vesselTypeMapper = vesselTypeMapper;
    this.vesselStatusMapper = vesselStatusMapper;
  }

  @Override
  public AvailableFiltersDTO getAvailableFilters() {
    return AvailableFiltersDTO.builder()
        .filterFrom(List.of(FilterFromEnum.ALL.getDescription(), FilterFromEnum.MY_FLEET.getDescription()))
        .vesselTypes(vesselTypeService.findAllVesselTypes().stream().map(vesselTypeMapper::toSelectOptionDTO).toList())
        .vesselStatuses(vesselStatusService.findAllVesselStatuses().stream().map(vesselStatusMapper::toSelectOptionDTO).toList())
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
  public List<VesselHistoryData> getVesselHistoryDataFiltered() {
    Optional<RegisteredUser> optionalRegisteredUser = Optional.ofNullable(seeSeaUserDetailsService.getUserDetails())
        .map(user -> registeredUserService.getRegisteredUserById(user.getId()));
    Filters filters = getFilters(optionalRegisteredUser);
    List<Long> vesselTypeIds = getVesselTypeIds(filters);
    List<Long> vesselStatusIds = getVesselStatusIds(filters);
    if (FilterFromEnum.MY_FLEET.getDescription().equals(getFilterFrom(filters))) {
      List<String> mmsisFromFleet = getMmsisFromFleet(optionalRegisteredUser);
      return filtersRepository.getVesselHistoryDataFiltered(vesselTypeIds, vesselStatusIds)
          .stream().filter(data -> mmsisFromFleet.contains(data.getVessel().getMmsi())).toList();
    }
    return filtersRepository.getVesselHistoryDataFiltered(vesselTypeIds, vesselStatusIds);
  }

  @Override
  public CurrentFiltersDTO getCurrentFilters() {
    Filters filters = filtersRepository.findByRegisteredUserId(seeSeaUserDetailsService.getUserDetails().getId());
    if (Objects.isNull(filters)) {
      return CurrentFiltersDTO.builder()
          .filterFrom(FilterFromEnum.ALL.getDescription())
          .vesselTypeIds(List.of())
          .vesselStatusIds(List.of())
          .build();
    }
    return CurrentFiltersDTO.builder()
        .filterFrom(filters.getFilterFrom())
        .vesselTypeIds(Optional.ofNullable(filters.getVesselTypes())
                           .orElse(List.of())
                           .stream().map(VesselType::getId).toList())
        .vesselStatusIds(Optional.ofNullable(filters.getVesselStatuses())
                             .orElse(List.of())
                             .stream().map(VesselStatus::getId).toList())
        .build();
  }

  private static List<String> getMmsisFromFleet(Optional<RegisteredUser> optionalRegisteredUser) {
    return optionalRegisteredUser.map(RegisteredUser::getVessels)
        .map(vessels -> vessels.stream().map(Vessel::getMmsi).toList())
        .orElse(List.of());
  }

  private static String getFilterFrom(Filters filters) {
    return Optional.ofNullable(filters).map(Filters::getFilterFrom)
        .orElse(FilterFromEnum.ALL.getDescription());
  }

  private List<Long> getVesselStatusIds(Filters filters) {
    List<VesselStatus> vesselStatuses = Optional.ofNullable(filters)
        .map(Filters::getVesselStatuses)
        .filter(list -> !list.isEmpty())
        .orElseGet(vesselStatusService::findAllVesselStatuses);
    return vesselStatuses.stream().map(VesselStatus::getId).toList();
  }

  private List<Long> getVesselTypeIds(Filters filters) {
    List<VesselType> vesselTypes = Optional.ofNullable(filters)
        .map(Filters::getVesselTypes)
        .filter(list -> !list.isEmpty())
        .orElseGet(vesselTypeService::findAllVesselTypes);
    return vesselTypes.stream().map(VesselType::getId).toList();
  }

  private static Filters getFilters(Optional<RegisteredUser> optionalRegisteredUser) {
    return optionalRegisteredUser
        .map(RegisteredUser::getFilters)
        .orElse(null);
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