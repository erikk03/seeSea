package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.AlertDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.services.implementation.LocationsConsumerImpl;
import gr.uoa.di.ships.services.interfaces.CollisionWarningService;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.NotificationService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LocationsConsumerImplTest {

  @Mock
  private SimpMessagingTemplate template;

  @Mock
  private VesselHistoryDataService vesselHistoryDataService;

  @Mock
  private RegisteredUserService registeredUserService;

  @Mock
  private FiltersService filtersService;

  @Mock
  private VesselStatusService vesselStatusService;

  @Mock
  private VesselService vesselService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private CollisionWarningService collisionWarningService;

  @InjectMocks
  private LocationsConsumerImpl locationsConsumer;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(locationsConsumer, "objectMapper", objectMapper);
  }

  @Test
  void consume_shouldProcessValidMessage() throws Exception {
    // Prepare
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"vesselType":"Cargo"}
        """;

    JsonNode jsonNode = new ObjectMapper().readTree(message);
    ObjectNode tunedJsonNode = jsonNode.deepCopy();

    Vessel vessel = new Vessel("123456789", VesselType.builder().name("Cargo").build());
    tunedJsonNode.put("vesselType", "Cargo");
    tunedJsonNode.put("status", "Active");

    when(vesselService.getVesselByMMSI("123456789")).thenReturn(Optional.of(vessel));
    when(vesselStatusService.getVesselStatusById(1L)).thenReturn(VesselStatus.builder().name("Active").build());

    when(registeredUserService.getAllUsersIds()).thenReturn(List.of(1L));
    when(filtersService.compliesWithUserFilters(jsonNode, 1L)).thenReturn(true);

    RegisteredUser user = new RegisteredUser();
    user.setId(1L);
    user.setZoneOfInterest(ZoneOfInterest.builder().build());
    user.setZoneOfInterestOptions(ZoneOfInterestOptions.builder().maxSpeed(20f).build());
    when(vesselHistoryDataService.getLastVesselHistoryDataForMmsi("123456789")).thenReturn(Optional.empty());
    when(notificationService.violatesMaxSpeed(eq(user), any(), any())).thenReturn(true);
    when(notificationService.entersZone(eq(user), any(), any())).thenReturn(true);
    when(notificationService.exitsZone(eq(user), any(), any())).thenReturn(true);
    when(registeredUserService.getRegisteredUserById(anyLong())).thenReturn(user);

    // Execute
    locationsConsumer.consume(message);

    // Verify
    verify(template, times(1)).convertAndSend("/topic/locations", tunedJsonNode.toPrettyString());
    verify(template, times(1)).convertAndSendToUser("1", "/queue/locations", tunedJsonNode);

    ArgumentCaptor<AlertDTO> alertCaptor = ArgumentCaptor.forClass(AlertDTO.class); // Capture and assert AlertDTO
    verify(template, times(1)).convertAndSendToUser(eq("1"), eq("/queue/alerts"), alertCaptor.capture());
    AlertDTO sentAlert = alertCaptor.getValue();
    assertEquals("123456789", sentAlert.getVesselMmsi());
    assertEquals(1L, sentAlert.getUserId());
    assertNotNull(sentAlert.getAlertDescriptions());
    assertEquals(3, sentAlert.getAlertDescriptions().size());
  }

  @Test
  void consume_shouldLogErrorForInvalidJson() {
    // Arrange
    String invalidMessage = "INVALID_JSON";

    // Act
    locationsConsumer.consume(invalidMessage);

    // Assert
    // Expect no exception thrown, just log output
    verifyNoInteractions(template);
  }
}
