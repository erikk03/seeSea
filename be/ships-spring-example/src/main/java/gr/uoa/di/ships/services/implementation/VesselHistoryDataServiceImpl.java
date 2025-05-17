package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.repository.VesselHistoryDataRepository;
import gr.uoa.di.ships.services.interfaces.VesselHistoryDataService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class VesselHistoryDataServiceImpl implements VesselHistoryDataService {

  private final VesselHistoryDataRepository vesselHistoryDataRepository;
  private final VesselHistoryDataMapper vesselHistoryDataMapper;

  public VesselHistoryDataServiceImpl(VesselHistoryDataRepository vesselHistoryDataRepository, VesselHistoryDataMapper vesselHistoryDataMapper) {
    this.vesselHistoryDataRepository = vesselHistoryDataRepository;
    this.vesselHistoryDataMapper = vesselHistoryDataMapper;
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
  public List<VesselHistoryDataDTO> getMap(FiltersDTO filtersDTO) {
    return vesselHistoryDataRepository.getLastVesselHistoryDataPerVessel()
        .stream()
        .map(vesselHistoryDataMapper::toVesselHistoryDataDTO)
        .toList();
  }

  @Override
  public void deleteOldVesselHistoryData() {
    LocalDateTime datetimeNow = LocalDateTime.now(ZoneOffset.UTC);
    vesselHistoryDataRepository.deleteByDatetimeCreatedBefore(datetimeNow.minusHours(12));
  }
}