package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.LocationsConsumer;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class LocationsConsumerImpl implements LocationsConsumer {

  private final ObjectMapper objectMapper;
  private final List<JsonNode> buffer = Collections.synchronizedList(new ArrayList<>());
  private final AtomicInteger batchCount = new AtomicInteger(0);

  private final SimpMessagingTemplate template;
  private final VesselHistoryDataService vesselHistoryDataService;
  private final RegisteredUserService registeredUserService;
  private final FiltersService filtersService;
  private final VesselStatusService vesselStatusService;
  private final VesselService vesselService;

  public LocationsConsumerImpl(ObjectMapper objectMapper,
                               SimpMessagingTemplate template,
                               VesselHistoryDataService vesselHistoryDataService,
                               RegisteredUserService registeredUserService,
                               FiltersService filtersService,
                               VesselStatusService vesselStatusService,
                               VesselService vesselService) {
    this.objectMapper = objectMapper;
    this.template = template;
    this.vesselHistoryDataService = vesselHistoryDataService;
    this.registeredUserService = registeredUserService;
    this.filtersService = filtersService;
    this.vesselStatusService = vesselStatusService;
    this.vesselService = vesselService;
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
      handleBatches(jsonNode);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      log.error("Error while consuming Kafka message: {}", e.getMessage(), e);
    }
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
        .forEach(userId -> template.convertAndSendToUser(
            userId.toString(),
            "/queue/locations",
            tunedJsonNode));
  }

  private ObjectNode getTunedJsonNode(JsonNode jsonNode) {
    String mmsi = jsonNode.get("mmsi").asText();
    VesselType vesselType = vesselService.getVesselByMMSI(mmsi)
        .orElseGet(() -> vesselService.saveVessel(new Vessel(mmsi)))
        .getVesselType();
    ObjectNode tunedJsonNode = ((ObjectNode) jsonNode).deepCopy();
    tunedJsonNode.put("vesselType", Objects.nonNull(vesselType) ? vesselType.getName() : null);
    tunedJsonNode.put("status", vesselStatusService.getVesselStatusById(jsonNode.get("status").asLong()).getName());
    return tunedJsonNode;
  }

  @PreDestroy
  public void onShutdown() {
    synchronized (buffer) {
      if (!buffer.isEmpty()) {
        log.info("Flushing final batch of {} vessel history entries before shutdown.", buffer.size());
        vesselHistoryDataService.saveVesselHistoryData(new ArrayList<>(buffer));
        log.info("Saved final batch of {} vessel history entries to DB", buffer.size());
        buffer.clear();
        batchCount.set(0);
      }
    }
  }

  private void handleBatches(JsonNode jsonNode) {
    buffer.add(jsonNode);
    int currentCount = batchCount.incrementAndGet();
    int batchSize = 20;
    if (currentCount >= batchSize) {
      synchronized (buffer) {
        if (!buffer.isEmpty()) {
          vesselHistoryDataService.saveVesselHistoryData(new ArrayList<>(buffer));
          buffer.clear();
          batchCount.set(0);
          log.info("Saved {} vessel history entries to DB", batchSize);
        }
      }
    }
  }
}