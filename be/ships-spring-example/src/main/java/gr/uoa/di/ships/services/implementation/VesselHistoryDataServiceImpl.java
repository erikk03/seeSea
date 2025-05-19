package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.model.VesselType;
import gr.uoa.di.ships.persistence.repository.VesselHistoryDataRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.VesselHistoryDataService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class VesselHistoryDataServiceImpl implements VesselHistoryDataService {

  private final VesselHistoryDataRepository vesselHistoryDataRepository;
  private final VesselHistoryDataMapper vesselHistoryDataMapper;
  private final FiltersService filtersService;
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;
  private final RegisteredUserService registeredUserService;

  public VesselHistoryDataServiceImpl(VesselHistoryDataRepository vesselHistoryDataRepository,
                                      VesselHistoryDataMapper vesselHistoryDataMapper,
                                      FiltersService filtersService,
                                      SeeSeaUserDetailsService seeSeaUserDetailsService,
                                      RegisteredUserService registeredUserService) {
    this.vesselHistoryDataRepository = vesselHistoryDataRepository;
    this.vesselHistoryDataMapper = vesselHistoryDataMapper;
    this.filtersService = filtersService;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.registeredUserService = registeredUserService;
  }

  @Override
  public void saveVesselHistoryData(List<JsonNode> vesselHistoryDataList) {
    vesselHistoryDataRepository.saveAll(
        vesselHistoryDataList.stream()
            .map(vesselHistoryDataMapper::toVesselHistoryData)
            .toList()
    );
  }

  @Override
  public List<VesselHistoryDataDTO> setFiltersAndGetMap(FiltersDTO filtersDTO) {
    filtersService.persistFilters(filtersDTO);
    return getMap();
  }

  @Override
  public List<VesselHistoryDataDTO> getMap() {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    Filters filters = registeredUser.getFilters();
    List<VesselType> vesselTypes = Objects.nonNull(filters) ? filters.getVesselTypes() : List.of();
    List<VesselStatus> vesselStatuses = Objects.nonNull(filters) ? filters.getVesselStatuses() : List.of();
    return filtersService.getVesselHistoryDataFiltered(vesselTypes, vesselStatuses).stream()
        .map(vesselHistoryDataMapper::toVesselHistoryDataDTO)
        .toList();
  }

  @Override
  public void deleteOldVesselHistoryData() {
    LocalDateTime datetimeNow = LocalDateTime.now(ZoneOffset.UTC);
    vesselHistoryDataRepository.deleteByDatetimeCreatedBefore(datetimeNow.minusHours(12));
  }
}