package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.CurrentFiltersDTO;
import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselStatusMapper;
import gr.uoa.di.ships.api.mapper.interfaces.VesselTypeMapper;
import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.FiltersRepository;
import gr.uoa.di.ships.services.implementation.FiltersServiceImpl;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterServiceImplTest {

  @Mock
  private VesselTypeService vesselTypeService;

  @Mock
  private VesselStatusService vesselStatusService;

  @Mock
  private FiltersRepository filtersRepository;

  @Mock
  private RegisteredUserService registeredUserService;

  @Mock
  private VesselService vesselService;

  @Mock
  private SeeSeaUserDetailsService seeSeaUserDetailsService;

  @Mock
  private VesselTypeMapper vesselTypeMapper;

  @Mock
  private VesselStatusMapper vesselStatusMapper;

  @InjectMocks
  private FiltersServiceImpl filtersService;

  @Test
  void getAvailableFilters() {
    // Prepare
    when(vesselTypeService.findAllVesselTypes())
        .thenReturn(List.of(
            VesselType.builder().build()
        ));
    when(vesselTypeMapper.toSelectOptionDTO(any())).thenReturn(new SelectOptionDTO());

    when(vesselStatusService.findAllVesselStatuses())
        .thenReturn(List.of(
            VesselStatus.builder().build()
        ));
    when(vesselStatusMapper.toSelectOptionDTO(any())).thenReturn(new SelectOptionDTO());

    // Execute
    AvailableFiltersDTO result = filtersService.getAvailableFilters();

    // Verify
    assertNotNull(result);
    assertEquals(2, result.getFilterFrom().size());
    assertEquals(1, result.getVesselTypes().size());
    assertEquals(1, result.getVesselStatuses().size());
  }

  @Test
  void persistFilters_throwsIllegalArgumentException() {
    // Execute and Verify
    assertThrows(
        IllegalArgumentException.class,
        () -> filtersService.persistFilters(FiltersDTO.builder().filterFrom("wrongFilterFrom").build())
    );
  }

  @Test
  void persistFilters_filterExists() {
    // Prepare
    FiltersDTO filtersDTO = FiltersDTO.builder()
        .filterFrom("All")
        .vesselTypeIds(List.of(1L))
        .vesselStatusIds(List.of(1L))
        .build();

    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(
            RegisteredUser.builder()
                .id(1L)
                .build()
        );
    when(filtersRepository.findByRegisteredUserId(1L))
        .thenReturn(Filters.builder().build());
    when(vesselTypeService.findVesselTypesByIds(filtersDTO.getVesselTypeIds()))
        .thenReturn(List.of(VesselType.builder().id(1L).build()));
    when(vesselStatusService.getVesselStatusesByIds(filtersDTO.getVesselStatusIds()))
        .thenReturn(List.of(VesselStatus.builder().id(1L).build()));


    // Execute
    filtersService.persistFilters(filtersDTO);

    // Verify
   verify(filtersRepository).save(any(Filters.class));
  }

  @Test
  void persistFilters_filterDoesNotExists() {
    // Prepare
    FiltersDTO filtersDTO = FiltersDTO.builder()
        .filterFrom("All")
        .vesselTypeIds(List.of(1L))
        .vesselStatusIds(List.of(1L))
        .build();

    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(
            RegisteredUser.builder()
                .id(1L)
                .build()
        );
    when(filtersRepository.findByRegisteredUserId(1L))
        .thenReturn(null);
    when(vesselTypeService.findVesselTypesByIds(filtersDTO.getVesselTypeIds()))
        .thenReturn(List.of(VesselType.builder().id(1L).build()));
    when(vesselStatusService.getVesselStatusesByIds(filtersDTO.getVesselStatusIds()))
        .thenReturn(List.of(VesselStatus.builder().id(1L).build()));


    // Execute
    filtersService.persistFilters(filtersDTO);

    // Verify
    verify(filtersRepository).save(any(Filters.class));
  }

  @Test
  void compliesWithUserFilters_EmptyFilters_true() {
    // Prepare
    Long userId = 1L;
    String mmsi = "123456789";
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", mmsi);
    sampleJson.put("status", 1L);

    when(registeredUserService.getRegisteredUserById(userId))
        .thenReturn(RegisteredUser.builder()
                        .id(userId)
                        .vessels(Set.of(Vessel.builder().mmsi(mmsi).build()))
                        .filters(null)
                        .build());

    // Execute
    boolean result = filtersService.compliesWithUserFilters(sampleJson, userId);

    // Verify
    assertTrue(result);
  }

  @Test
  void compliesWithUserFilters_MyFleet_true() {
    // Prepare
    Long userId = 1L;
    String mmsi = "123456789";
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", mmsi);
    sampleJson.put("status", 1L);

    Filters filters = Filters.builder()
        .filterFrom("MyFleet")
        .vesselTypes(List.of())
        .vesselStatuses(List.of())
        .build();
    when(registeredUserService.getRegisteredUserById(userId))
        .thenReturn(RegisteredUser.builder()
                        .id(userId)
                        .vessels(Set.of(Vessel.builder().mmsi(mmsi).build()))
                        .filters(filters)
                        .build());

    // Execute
    boolean result = filtersService.compliesWithUserFilters(sampleJson, userId);

    // Verify
    assertTrue(result);
  }

  @Test
  void compliesWithUserFilters_vesselTypes_true() {
    // Prepare
    Long userId = 1L;
    String mmsi = "123456789";
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", mmsi);
    sampleJson.put("status", 1L);
    VesselType vesselType = VesselType.builder().name("tempVesselType").build();

    Filters filters = Filters.builder()
        .filterFrom("All")
        .vesselTypes(List.of(vesselType))
        .vesselStatuses(List.of())
        .build();
    when(registeredUserService.getRegisteredUserById(userId))
        .thenReturn(RegisteredUser.builder()
                        .id(userId)
                        .vessels(Set.of(Vessel.builder().mmsi(mmsi).build()))
                        .filters(filters)
                        .build());

    when(vesselService.getVesselByMMSI(mmsi))
        .thenReturn(Optional.of(Vessel.builder()
                                    .mmsi(mmsi)
                                    .vesselType(vesselType)
                                    .build()));

    // Execute
    boolean result = filtersService.compliesWithUserFilters(sampleJson, userId);

    // Verify
    assertTrue(result);
  }

  @Test
  void compliesWithUserFilters_vesselTypes_false() {
    // Prepare
    Long userId = 1L;
    String mmsi = "123456789";
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", mmsi);
    sampleJson.put("status", 1L);
    VesselType correctVesselType = VesselType.builder().name("correctVesselType").build();
    VesselType wrongVesselType = VesselType.builder().name("wrongVesselType").build();

    Filters filters = Filters.builder()
                             .filterFrom("All")
                             .vesselTypes(List.of(wrongVesselType))
                             .vesselStatuses(List.of())
                             .build();
    when(registeredUserService.getRegisteredUserById(userId))
        .thenReturn(RegisteredUser.builder()
                                  .id(userId)
                                  .vessels(Set.of(Vessel.builder().mmsi(mmsi).build()))
                                  .filters(filters)
                                  .build());

    when(vesselService.getVesselByMMSI(mmsi))
        .thenReturn(Optional.of(Vessel.builder()
                                      .mmsi(mmsi)
                                      .vesselType(correctVesselType)
                                      .build()));

    // Execute
    boolean result = filtersService.compliesWithUserFilters(sampleJson, userId);

    // Verify
    assertFalse(result);
  }

  @Test
  void compliesWithUserFilters_vesselStatues_true() {
    // Prepare
    Long userId = 1L;
    String mmsi = "123456789";
    ObjectMapper jacksonMapper = new ObjectMapper();
    ObjectNode sampleJson = jacksonMapper.createObjectNode();
    sampleJson.put("mmsi", mmsi);
    Long statusId = 1L;
    sampleJson.put("status", statusId);
    VesselStatus vesselStatus = VesselStatus.builder().id(statusId).build();

    Filters filters = Filters.builder()
                             .filterFrom("All")
                             .vesselTypes(List.of())
                             .vesselStatuses(List.of(vesselStatus))
                             .build();
    when(registeredUserService.getRegisteredUserById(userId))
        .thenReturn(RegisteredUser.builder()
                                  .id(userId)
                                  .vessels(Set.of(Vessel.builder().mmsi(mmsi).build()))
                                  .filters(filters)
                                  .build());

    // Execute
    boolean result = filtersService.compliesWithUserFilters(sampleJson, userId);

    // Verify
    assertTrue(result);
  }

  @Test
  void getVesselHistoryDataFiltered_nullRegisteredUser() {
    // Prepare
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(null);
    when(vesselTypeService.findAllVesselTypes())
        .thenReturn(List.of(
            VesselType.builder().id(1L).build()
        ));
    when(vesselStatusService.findAllVesselStatuses())
        .thenReturn(List.of(
            VesselStatus.builder().id(1L).build()
        ));
    when(filtersRepository.getVesselHistoryDataFiltered(any(), any()))
        .thenReturn(List.of(new VesselHistoryData()));

    // Execute
    List<VesselHistoryData> result = filtersService.getVesselHistoryDataFiltered();

    // Verify
    assertNotNull(result);
  }

  @Test
  void getVesselHistoryDataFiltered_withRegisteredUser() {
    // Prepare
    Filters filters = Filters.builder()
        .filterFrom("MyFleet")
        .vesselTypes(List.of(VesselType.builder().id(1L).build()))
        .vesselStatuses(List.of(VesselStatus.builder().id(1L).build()))
        .build();
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(Set.of(vessel))
        .filters(filters)
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);
    when(filtersRepository.getVesselHistoryDataFiltered(any(), any()))
        .thenReturn(List.of(VesselHistoryData.builder().vessel(vessel).build()));

    // Execute
    List<VesselHistoryData> result = filtersService.getVesselHistoryDataFiltered();

    // Verify
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(vessel.getMmsi(), result.getFirst().getVessel().getMmsi());
  }

  @Test
  void getCurrentFilters_filtersNull() {
    // Prepare
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .filters(null)
        .build();

    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(filtersRepository.findByRegisteredUserId(registeredUser.getId()))
        .thenReturn(null);

    // Execute
    CurrentFiltersDTO result = filtersService.getCurrentFilters();

    // Verify
    assertNotNull(result);
    assertEquals("All", result.getFilterFrom());
    assertTrue(result.getVesselTypeIds().isEmpty());
    assertTrue(result.getVesselStatusIds().isEmpty());
  }

  @Test
  void getCurrentFilters() {
    // Prepare
    Filters filters = Filters.builder()
        .filterFrom("MyFleet")
        .vesselTypes(List.of(VesselType.builder().id(1L).build()))
        .vesselStatuses(List.of(VesselStatus.builder().id(1L).build()))
        .build();
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(Set.of(vessel))
        .filters(filters)
        .build();

    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(filtersRepository.findByRegisteredUserId(registeredUser.getId()))
        .thenReturn(filters);

    // Execute
    CurrentFiltersDTO result = filtersService.getCurrentFilters();

    // Verify
    assertNotNull(result);
    assertEquals("MyFleet", result.getFilterFrom());
    assertEquals(1, result.getVesselTypeIds().size());
    assertEquals(1, result.getVesselStatusIds().size());
  }
}
