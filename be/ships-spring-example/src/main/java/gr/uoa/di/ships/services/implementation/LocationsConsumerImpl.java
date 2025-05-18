package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import gr.uoa.di.ships.services.interfaces.LocationsConsumer;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.VesselHistoryDataService;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

  public LocationsConsumerImpl(ObjectMapper objectMapper,
                               SimpMessagingTemplate template,
                               VesselHistoryDataService vesselHistoryDataService,
                               RegisteredUserService registeredUserService,
                               FiltersService filtersService) {
    this.objectMapper = objectMapper;
    this.template = template;
    this.vesselHistoryDataService = vesselHistoryDataService;
    this.registeredUserService = registeredUserService;
    this.filtersService = filtersService;
  }

  @KafkaListener(topics = "${kafka.topic}")
  @Override
  public void consume(String message) {
    try {
      JsonNode jsonNode = objectMapper.readTree(message);
      sentToAnonymousUsers(jsonNode);
      sendToFilterCompliantRegisteredUsers(jsonNode);
      System.out.println("Sent message: " + jsonNode.toPrettyString());
      handleBatches(jsonNode);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      log.error("Error while consuming Kafka message: {}", e.getMessage(), e);
    }
  }

  private void sentToAnonymousUsers(JsonNode jsonNode) {
    template.convertAndSend("/topic/locations", jsonNode.toPrettyString());
  }

  private void sendToFilterCompliantRegisteredUsers(JsonNode jsonNode) {
    registeredUserService.getAllRegisteredUsers().stream()
        .filter(user -> filtersService.compliesWithUserFilters(jsonNode, user.getId()))
        .forEach(user -> {
          template.convertAndSendToUser(user.getUsername(), "/queue/locations", jsonNode.toPrettyString());
        });
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