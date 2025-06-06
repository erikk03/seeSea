package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.api.mapper.interfaces.NotificationMapper;
import gr.uoa.di.ships.configurations.exceptions.NotificationNotFoundException;
import gr.uoa.di.ships.persistence.model.Notification;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.repository.NotificationRepository;
import gr.uoa.di.ships.services.implementation.NotificationServiceImpl;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private SeeSeaUserDetailsService seeSeaUserDetailsService;

  @Mock
  private RegisteredUserService registeredUserService;

  @Mock
  private NotificationMapper notificationMapper;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @Test
  void getAllNotifications() {
    // Prepare
    RegisteredUser registeredUser = RegisteredUser.builder().id(1L).build();
    when(seeSeaUserDetailsService.getUserDetails()).thenReturn(registeredUser);
    Notification notification = Notification.builder().id(1L).build();
    when(notificationRepository.findAllByRegisteredUser_Id(registeredUser.getId()))
        .thenReturn(List.of(notification));
    when(notificationMapper.toNotificationDTO(notification))
        .thenReturn(NotificationDTO.builder().id(notification.getId()).build());

    // Execute
    List<NotificationDTO> result = notificationService.getAllNotifications();

    // Verify
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(notification.getId(), result.getFirst().getId());
  }

  @Test
  void deleteNotification() {
    // Prepare
    RegisteredUser registeredUser = RegisteredUser.builder().id(1L).build();
    when(seeSeaUserDetailsService.getUserDetails()).thenReturn(registeredUser);
    Long notificationId = 1L;
    when(notificationRepository.findById(notificationId))
        .thenReturn(Optional.of(Notification.builder().id(notificationId).registeredUser(registeredUser).build()));

    // Execute
    notificationService.deleteNotification(notificationId);

    // Verify
    verify(notificationRepository, times(1)).deleteById(notificationId);
  }

  @Test
  void deleteNotification_throwsNotificationNotFoundException() {
    // Prepare
    RegisteredUser registeredUser = RegisteredUser.builder().id(1L).build();
    when(seeSeaUserDetailsService.getUserDetails()).thenReturn(registeredUser);
    Long notificationId = 1L;
    when(notificationRepository.findById(notificationId))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        NotificationNotFoundException.class,
        () -> notificationService.deleteNotification(notificationId)
    );
  }

  @Test
  void deleteNotification_throwsRuntimeException() {
    // Prepare
    RegisteredUser registeredUser = RegisteredUser.builder().id(1L).build();
    when(seeSeaUserDetailsService.getUserDetails()).thenReturn(registeredUser);
    Long notificationId = 1L;
    when(notificationRepository.findById(notificationId))
        .thenReturn(Optional.of(
            Notification.builder()
                .id(notificationId)
                .registeredUser(RegisteredUser.builder().id(2L).build())
                .build())
        );

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> notificationService.deleteNotification(notificationId)
    );
  }

  @Test
  void saveNotification() {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .notifications(new HashSet<>(Set.of()))
        .build();

    // Execute
    notificationService.saveNotification("Test Notification", user);

    // Verify
    verify(notificationRepository, times(1)).save(any());
  }

  @Test
  void violatesMaxSpeed_false_radiusSmaller() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(1d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    VesselHistoryData previousVesselData = VesselHistoryData.builder().build();

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertFalse(result);
  }

  @Test
  void violatesMaxSpeed_false_zoneOfInterestOptionsNull() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .maxSpeed(null)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    VesselHistoryData previousVesselData = VesselHistoryData.builder().build();

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertFalse(result);
  }

  @Test
  void violatesMaxSpeed_false_previousVesselDataNull() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .maxSpeed(30f)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, null);

    // Verify
    assertFalse(result);
  }

  @Test
  void violatesMaxSpeed_true_isBefore() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .maxSpeed(10f)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    VesselHistoryData previousVesselData = VesselHistoryData.builder()
        .datetimeCreated(user.getZoneOfInterest().getDatetimeCreated().minusDays(1))
        .build();

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertTrue(result);
  }

  @Test
  void violatesMaxSpeed_true_previousSpeedLess() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .maxSpeed(10f)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    VesselHistoryData previousVesselData = VesselHistoryData.builder()
        .datetimeCreated(user.getZoneOfInterest().getDatetimeCreated().plusDays(1))
        .speed(2f)
        .build();

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertTrue(result);
  }

  @Test
  void violatesMaxSpeed_false_allOptionsFalse() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .maxSpeed(10f)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    VesselHistoryData previousVesselData = VesselHistoryData.builder()
        .datetimeCreated(user.getZoneOfInterest().getDatetimeCreated().plusDays(1))
        .speed(20f)
        .build();

    // Execute
    boolean result = notificationService.violatesMaxSpeed(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertFalse(result);
  }

  @Test
  void entersZone_false_disabled() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .entersZone(false)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    // Execute
    boolean result = notificationService.entersZone(user, (ObjectNode) jsonNode, null);

    // Verify
    assertFalse(result);
  }

  @Test
  void entersZone_true_nullPrevious() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .entersZone(true)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    // Execute
    boolean result = notificationService.entersZone(user, (ObjectNode) jsonNode, null);

    // Verify
    assertTrue(result);
  }

  @Test
  void entersZone_true() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(3000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .entersZone(true)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);
    VesselHistoryData previousVesselData = VesselHistoryData.builder()
        .latitude(100d)
        .longitude(100d)
        .build();

    // Execute
    boolean result = notificationService.entersZone(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertTrue(result);
  }

  @Test
  void exitsZone_false_disabled() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .exitsZone(false)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    // Execute
    boolean result = notificationService.exitsZone(user, (ObjectNode) jsonNode, null);

    // Verify
    assertFalse(result);
  }

  @Test
  void exitsZone_false_nullPrevious() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(100000000000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .exitsZone(true)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20.5,"lat":23,"lon":13,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);

    // Execute
    boolean result = notificationService.exitsZone(user, (ObjectNode) jsonNode, null);

    // Verify
    assertFalse(result);
  }

  @Test
  void exitsZone_true() throws JsonProcessingException {
    // Prepare
    RegisteredUser user = RegisteredUser.builder()
        .id(1L)
        .zoneOfInterest(ZoneOfInterest.builder()
                            .radius(3000000d)
                            .centerPointLongitude(1d)
                            .centerPointLatitude(1d)
                            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                            .build())
        .zoneOfInterestOptions(ZoneOfInterestOptions.builder()
                                   .exitsZone(true)
                                   .build())
        .build();
    String message = """
            {"mmsi":"123456789","status":"1","speed":20,"lat":100,"lon":100,"vesselType":"Cargo"}
        """;
    JsonNode jsonNode = new ObjectMapper().readTree(message);
    VesselHistoryData previousVesselData = VesselHistoryData.builder()
        .latitude(2d)
        .longitude(2d)
        .build();

    // Execute
    boolean result = notificationService.exitsZone(user, (ObjectNode) jsonNode, previousVesselData);

    // Verify
    assertTrue(result);
  }
}
