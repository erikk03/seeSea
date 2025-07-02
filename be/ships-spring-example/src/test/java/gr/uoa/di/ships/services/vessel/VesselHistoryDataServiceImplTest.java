package gr.uoa.di.ships.services.vessel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselHistoryDataMapper;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.repository.vessel.VesselHistoryDataRepository;
import gr.uoa.di.ships.services.implementation.vessel.VesselHistoryDataServiceImpl;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VesselHistoryDataServiceImplTest {

  @Mock
  private VesselHistoryDataRepository vesselHistoryDataRepository;

  @Mock
  private VesselHistoryDataMapper vesselHistoryDataMapper;

  @Mock
  private FiltersService filtersService;

  @InjectMocks
  private VesselHistoryDataServiceImpl vesselHistoryDataService;

  @Test
  void saveVesselHistoryData() {
    // Prepare
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", "123");

    when(vesselHistoryDataMapper.toVesselHistoryData(sampleJson))
        .thenReturn(new VesselHistoryData());

    // Execute
    vesselHistoryDataService.saveVesselHistoryData(sampleJson);

    // Verify
    verify(vesselHistoryDataRepository).save(any(VesselHistoryData.class));
  }

  @Test
  void setFiltersAndGetMap() {
    // Prepare
    FiltersDTO filtersDTO = FiltersDTO.builder().filterFrom("All").build();

    when(filtersService.getVesselHistoryDataFiltered())
        .thenReturn(List.of(VesselHistoryData.builder().build()));

    when(vesselHistoryDataMapper.toVesselHistoryDataDTO(any()))
        .thenReturn(VesselHistoryDataDTO.builder().build());

    // Execute
    List<VesselHistoryDataDTO> result = vesselHistoryDataService.setFiltersAndGetMap(filtersDTO);

    // Verify
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void deleteOldVesselHistoryData_shouldCallRepositoryWithDatetimeNowMinus12Hours() {
    // Prepare
    LocalDateTime earliestExpected = LocalDateTime.now(ZoneOffset.UTC).minusHours(12);

    // Execute
    vesselHistoryDataService.deleteOldVesselHistoryData();

    // Verify
    // Capture the argument passed to deleteByDatetimeCreatedBefore(...)
    ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
    verify(vesselHistoryDataRepository).deleteByDatetimeCreatedBefore(captor.capture());
    LocalDateTime capturedCutoff = captor.getValue();

    // Capture the “latest” cutoff just after invoking the method
    LocalDateTime latestExpected = LocalDateTime.now(ZoneOffset.UTC).minusHours(12);

    assertFalse(
        capturedCutoff.isBefore(earliestExpected),
        "Expected cutoff not to be before now minus 12 hours (earliest bound)"
    );
    assertFalse(
        capturedCutoff.isAfter(latestExpected),
        "Expected cutoff not to be after now minus 12 hours (latest bound)"
    );
  }

  @Test
  void getLastVesselHistoryDataListForMmsi() {
    // Prepare
    String mmsi = "123456789";

    when(vesselHistoryDataRepository.findLastVesselHistoryDataForMmsi(eq(mmsi)))
        .thenReturn(Optional.of(VesselHistoryData.builder().vessel(Vessel.builder().mmsi(mmsi).build()).build()));

    // Execute
    Optional<VesselHistoryData> result = vesselHistoryDataService.getLastVesselHistoryDataForMmsi(mmsi);

    // Verify
    assertNotNull(result);
    assertEquals(mmsi, result.get().getVessel().getMmsi());
  }

  @Test
  void getVesselHistoryForTwelveHours() {
    // Prepare
    String mmsi = "123456789";
    LocalDateTime baseTime = LocalDateTime.now(ZoneOffset.UTC);
    VesselHistoryData vesselHistoryData1 = VesselHistoryData.builder()
        .vessel(Vessel.builder().mmsi(mmsi).build())
        .speed(1f)
        .datetimeCreated(baseTime.minusHours(1))
        .build();
    VesselHistoryData vesselHistoryData2 = VesselHistoryData.builder()
        .vessel(Vessel.builder().mmsi(mmsi).build())
        .speed(2f)
        .datetimeCreated(baseTime.minusMinutes(30))
        .build();

    when(vesselHistoryDataRepository.findVesselHistoryDataByVessel_Mmsi(eq(mmsi)))
        .thenReturn(List.of(vesselHistoryData2, vesselHistoryData1));

    when(vesselHistoryDataMapper.toVesselHistoryDataDTO(vesselHistoryData2))
        .thenReturn(VesselHistoryDataDTO.builder().speed(2f).build());
    when(vesselHistoryDataMapper.toVesselHistoryDataDTO(vesselHistoryData1))
        .thenReturn(VesselHistoryDataDTO.builder().speed(1f).build());

    // Execute
    List<VesselHistoryDataDTO> result = vesselHistoryDataService.getVesselHistoryForTwelveHours(mmsi);

    // Verify
    assertNotNull(result);
    assertEquals(2f, result.getFirst().getSpeed());
    assertEquals(2f, result.size());
  }
}
