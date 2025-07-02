package gr.uoa.di.ships.services.implementation.vessel;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.repository.vessel.VesselHistoryDataRepository;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
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

  public VesselHistoryDataServiceImpl(VesselHistoryDataRepository vesselHistoryDataRepository,
                                      VesselHistoryDataMapper vesselHistoryDataMapper,
                                      FiltersService filtersService) {
    this.vesselHistoryDataRepository = vesselHistoryDataRepository;
    this.vesselHistoryDataMapper = vesselHistoryDataMapper;
    this.filtersService = filtersService;
  }

  @Override
  public void saveVesselHistoryData(JsonNode vesselHistoryData) {
    vesselHistoryDataRepository.save(vesselHistoryDataMapper.toVesselHistoryData(vesselHistoryData));
  }

  @Override
  public List<VesselHistoryDataDTO> setFiltersAndGetMap(FiltersDTO filtersDTO) {
    filtersService.persistFilters(filtersDTO);
    return getMap();
  }

  @Override
  public List<VesselHistoryDataDTO> getMap() {
    return filtersService.getVesselHistoryDataFiltered().stream()
        .map(vesselHistoryDataMapper::toVesselHistoryDataDTO)
        .toList();
  }

  @Override
  public void deleteOldVesselHistoryData() {
    LocalDateTime datetimeNow = LocalDateTime.now(ZoneOffset.UTC);
    vesselHistoryDataRepository.deleteByDatetimeCreatedBefore(datetimeNow.minusHours(12));
  }

  @Override
  public Optional<VesselHistoryData> getLastVesselHistoryDataForMmsi(String mmsi) {
    return vesselHistoryDataRepository.findLastVesselHistoryDataForMmsi(mmsi);
  }

  @Override
  public List<VesselHistoryData> getLastVesselHistoryDataList() {
    return vesselHistoryDataRepository.findLastVesselHistoryData();
  }

  @Override
  public List<VesselHistoryDataDTO> getVesselHistoryForTwelveHours(String mmsi) {
    return vesselHistoryDataRepository.findVesselHistoryDataByVessel_Mmsi(mmsi).stream()
        .filter(vhd -> vhd.getDatetimeCreated().isAfter(LocalDateTime.now(ZoneOffset.UTC).minusHours(12)))
        .map(vesselHistoryDataMapper::toVesselHistoryDataDTO)
        .toList();
  }
}