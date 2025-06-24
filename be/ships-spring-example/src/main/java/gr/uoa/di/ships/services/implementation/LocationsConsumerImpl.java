package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.AlertDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.services.interfaces.CollisionWarningService;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.LocationsConsumer;
import gr.uoa.di.ships.services.interfaces.NotificationService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class LocationsConsumerImpl implements LocationsConsumer {

  private static final String UNKNOWN = "unknown";
  private static final String SPEED_ALERT_DESCRIPTION = "Speed Alert: The vessel [mmsi: %s, speed: %s] is exceeding the maximum speed limit of %s.";
  private static final String ENTERS_ZONE_DESCRIPTION = "Enter Zone Alert: The vessel [mmsi: %s] has entered the zone of interest.";
  private static final String EXITS_ZONE_DESCRIPTION = "Exit Zone Alert: The vessel [mmsi: %s] has exited the zone of interest.";
  private static final String COLLISION_WARNING_DESCRIPTION = "Collision Warning Alert: The vessel [mmsi: %s] is in collision warning range with the following vessels: %s";
  private final ObjectMapper objectMapper;

  private final SimpMessagingTemplate template;
  private final VesselHistoryDataService vesselHistoryDataService;
  private final RegisteredUserService registeredUserService;
  private final FiltersService filtersService;
  private final VesselStatusService vesselStatusService;
  private final VesselService vesselService;
  private final VesselTypeService vesselTypeService;
  private final NotificationService notificationService;
  private final CollisionWarningService collisionWarningService;

  public LocationsConsumerImpl(ObjectMapper objectMapper,
                               SimpMessagingTemplate template,
                               VesselHistoryDataService vesselHistoryDataService,
                               RegisteredUserService registeredUserService,
                               FiltersService filtersService,
                               VesselStatusService vesselStatusService,
                               VesselService vesselService,
                               VesselTypeService vesselTypeService,
                               NotificationService notificationService,
                               CollisionWarningService collisionWarningService) {
    this.objectMapper = objectMapper;
    this.template = template;
    this.vesselHistoryDataService = vesselHistoryDataService;
    this.registeredUserService = registeredUserService;
    this.filtersService = filtersService;
    this.vesselStatusService = vesselStatusService;
    this.vesselService = vesselService;
    this.vesselTypeService = vesselTypeService;
    this.notificationService = notificationService;
    this.collisionWarningService = collisionWarningService;
  }

  @KafkaListener(topics = "${kafka.topic}")
  @Override
  public void consume(String message) {
    try {
      JsonNode jsonNode = objectMapper.readTree(message);
      ObjectNode jsonNodeToBeSent = getTunedJsonNode(jsonNode);
      sentToAnonymousUsers(jsonNodeToBeSent);
      sendToFilterCompliantRegisteredUsers(jsonNode, jsonNodeToBeSent);
      System.out.println("Sent message: " + jsonNodeToBeSent.toPrettyString());
      vesselHistoryDataService.saveVesselHistoryData(jsonNode);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      log.error("Error while consuming Kafka message: {}", e.getMessage(), e);
    }
  }

  private void sendUserAlerts(ObjectNode jsonNodeToBeSent, RegisteredUser user) {
    if (Objects.isNull(user.getZoneOfInterest())) {
      return;
    }
    List<String> alertDescriptions = getAlertDescriptions(user, jsonNodeToBeSent);
    if (!alertDescriptions.isEmpty()) {
      template.convertAndSendToUser(
          user.getId().toString(),
          "/queue/alerts",
          createZoneViolations(user, jsonNodeToBeSent, alertDescriptions)
      );
    }
  }

  private AlertDTO createZoneViolations(RegisteredUser user, ObjectNode jsonNodeToBeSent, List<String> alertDescriptions) {
    alertDescriptions.forEach(alertDescription -> notificationService.saveNotification(alertDescription, user));
    return AlertDTO.builder()
        .userId(user.getId())
        .vesselMmsi(jsonNodeToBeSent.get("mmsi").asText())
        .alertDescriptions(alertDescriptions)
        .build();
  }

  private List<String> getAlertDescriptions(RegisteredUser user, ObjectNode jsonNodeToBeSent) {
    List<String> alertDescriptions = new ArrayList<>();
    String mmsi = jsonNodeToBeSent.get("mmsi").asText();
    VesselHistoryData previousVesselData = vesselHistoryDataService.getLastVesselHistoryDataForMmsi(mmsi)
        .orElse(null);
    if (notificationService.violatesMaxSpeed(user, jsonNodeToBeSent, previousVesselData)) {
      alertDescriptions.add(SPEED_ALERT_DESCRIPTION.formatted(mmsi, jsonNodeToBeSent.get("speed").asDouble(), user.getZoneOfInterestOptions().getMaxSpeed()));
    }
    if (notificationService.entersZone(user, jsonNodeToBeSent, previousVesselData)) {
      alertDescriptions.add(ENTERS_ZONE_DESCRIPTION.formatted(mmsi));
    }
    if (notificationService.exitsZone(user, jsonNodeToBeSent, previousVesselData)) {
      alertDescriptions.add(EXITS_ZONE_DESCRIPTION.formatted(mmsi));
    }
    List<String> vesselsMmsis = collisionWarningService.collisionWarningWithVessels(user, jsonNodeToBeSent, previousVesselData);
    if (!vesselsMmsis.isEmpty()) {
      alertDescriptions.add(COLLISION_WARNING_DESCRIPTION.formatted(mmsi, getStringFromList(vesselsMmsis)));
    }
    return alertDescriptions;
  }

  private static String getStringFromList(List<String> items) {
    return "[" + String.join(", ", items) + "]";
  }

  private void sentToAnonymousUsers(JsonNode jsonNode) {
    template.convertAndSend(
        "/topic/locations",
        jsonNode.toPrettyString()
    );
  }

  private void sendToFilterCompliantRegisteredUsers(JsonNode jsonNode, ObjectNode tunedJsonNode) {
    registeredUserService.getAllUsersIds().stream()
        .filter(userId -> filtersService.compliesWithUserFilters(jsonNode, userId))
        .forEach(userId -> {
          sendUserAlerts(tunedJsonNode, registeredUserService.getRegisteredUserById(userId));
          template.convertAndSendToUser(userId.toString(), "/queue/locations", tunedJsonNode);
        });
  }

  private ObjectNode getTunedJsonNode(JsonNode jsonNode) {
    String mmsi = jsonNode.get("mmsi").asText();
    VesselType vesselType = vesselService.getVesselByMMSI(mmsi)
        .orElseGet(() -> vesselService.saveVessel(new Vessel(mmsi, vesselTypeService.findVesselTypeByName(UNKNOWN))))
        .getVesselType();
    ObjectNode tunedJsonNode = jsonNode.deepCopy();
    tunedJsonNode.put("vesselType", Objects.nonNull(vesselType) ? vesselType.getName() : null);
    tunedJsonNode.put("status", vesselStatusService.getVesselStatusById(jsonNode.get("status").asLong()).getName());
    return tunedJsonNode;
  }
}