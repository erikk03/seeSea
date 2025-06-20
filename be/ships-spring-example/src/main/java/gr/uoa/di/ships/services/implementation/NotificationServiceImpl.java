package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.api.mapper.interfaces.NotificationMapper;
import gr.uoa.di.ships.configurations.exceptions.NotificationNotFoundException;
import gr.uoa.di.ships.persistence.model.Notification;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.persistence.repository.NotificationRepository;
import gr.uoa.di.ships.services.interfaces.NotificationService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private static final String NOTIFICATION_S_DOES_NOT_BELONG_TO_THE_USER_WITH_ID_S = "Notification %s does not belong to the user with id %s";
  private final NotificationRepository notificationRepository;
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;
  private final NotificationMapper notificationMapper;
  private final RegisteredUserService registeredUserService;
  private final VesselHistoryDataService vesselHistoryDataService;

  public NotificationServiceImpl(NotificationRepository notificationRepository,
                                 SeeSeaUserDetailsService seeSeaUserDetailsService,
                                 NotificationMapper notificationMapper,
                                 RegisteredUserService registeredUserService,
                                 VesselHistoryDataService vesselHistoryDataService) {
    this.notificationRepository = notificationRepository;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.notificationMapper = notificationMapper;
    this.registeredUserService = registeredUserService;
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @Override
  public List<NotificationDTO> getAllNotifications() {
    return notificationRepository.findAllByRegisteredUser_Id(seeSeaUserDetailsService.getUserDetails().getId())
        .stream()
        .map(notificationMapper::toNotificationDTO)
        .toList();
  }

  @Override
  public void deleteNotification(Long id) {
    validateDeletion(id, seeSeaUserDetailsService.getUserDetails().getId());
    notificationRepository.deleteById(id);
    log.info("Notification with id {} deleted successfully", id);
  }

  @Override
  public void saveNotification(String description, RegisteredUser user) {
    Set<Notification> notifications = user.getNotifications();
    notifications.add(
        notificationRepository.save(
            Notification.builder()
                .description(description)
                .registeredUser(user)
                .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
                .build()));
    user.setNotifications(notifications);
    registeredUserService.updateRegisteredUser(user);
  }

  @Override
  public boolean violatesMaxSpeed(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    double currentDistance = getHaversineDistanceWithZoneOfInterestCenter(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    if (user.getZoneOfInterest().getRadius() < currentDistance || Objects.isNull(user.getZoneOfInterestOptions().getMaxSpeed())) {
      return false;
    }
    Double currentVesselSpeed = jsonNodeToBeSent.get("speed").asDouble();
    Float maxSpeed = user.getZoneOfInterestOptions().getMaxSpeed();
    if (Objects.isNull(previousVesselData)
        || previousVesselData.getDatetimeCreated().isBefore(user.getZoneOfInterest().getDatetimeCreated())
        || previousVesselData.getSpeed() < maxSpeed) {
      return maxSpeed < currentVesselSpeed;
    }
    return false;
  }

  @Override
  public boolean entersZone(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    if (!user.getZoneOfInterestOptions().isEntersZone()) {
      return false;
    }
    double currentDistance = getHaversineDistanceWithZoneOfInterestCenter(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    double radius = user.getZoneOfInterest().getRadius();
    if (Objects.isNull(previousVesselData)) {
      return currentDistance <= radius;
    } else {
      double previousDistance = getHaversineDistanceWithZoneOfInterestCenter(user, previousVesselData.getLatitude(), previousVesselData.getLongitude());
      return previousDistance > radius && currentDistance <= radius;
    }
  }

  @Override
  public boolean exitsZone(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    if (!user.getZoneOfInterestOptions().isExitsZone()) {
      return false;
    }
    double currentDistance = getHaversineDistanceWithZoneOfInterestCenter(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    double radius = user.getZoneOfInterest().getRadius();
    if (Objects.isNull(previousVesselData)) {
      return false;
    } else {
      double previousDistance = getHaversineDistanceWithZoneOfInterestCenter(user, previousVesselData.getLatitude(), previousVesselData.getLongitude());
      return previousDistance <= radius && currentDistance > radius;
    }
  }

  @Override
  public List<String> collisionWarningWithVessels(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    List<String> vesselsMmsisWithCollisionWarning = new ArrayList<>();
    double currentDistance = getHaversineDistanceWithZoneOfInterestCenter(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    if (user.getZoneOfInterest().getRadius() < currentDistance || !user.getZoneOfInterestOptions().isExitsZone()) {
      return vesselsMmsisWithCollisionWarning;
    }

    //todo: add to this logic
    List<VesselHistoryData> vesselHistoryData = vesselHistoryDataService.getLastVesselHistoryData();
    vesselHistoryData.stream()
        .filter(historyData -> user.getZoneOfInterest().getRadius() >= getHaversineDistanceWithZoneOfInterestCenter(user, historyData.getLatitude(), historyData.getLongitude()))
        .forEach(historyData -> {
          double distanceBetweenVessels = calculateHaversineDistance(
              historyData.getLatitude(), historyData.getLongitude(), jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble()
          );
          if (distanceBetweenVessels < 100000 && !historyData.getVessel().getMmsi().equals(jsonNodeToBeSent.get("mmsi").asText())) {
            vesselsMmsisWithCollisionWarning.add(historyData.getVessel().getMmsi());
          }
        });

    return vesselsMmsisWithCollisionWarning;
  }

  private static double getHaversineDistanceWithZoneOfInterestCenter(RegisteredUser user, double vesselLatitude, double vesselLongitude) {
    double zoiLatitude = user.getZoneOfInterest().getCenterPointLatitude();
    double zoiLongitude = user.getZoneOfInterest().getCenterPointLongitude();
    return calculateHaversineDistance(vesselLatitude, vesselLongitude, zoiLatitude, zoiLongitude);
  }

  private static double calculateHaversineDistance(double vesselLatitude, double vesselLongitude, double latitude, double longitude) {
    double R = 6371000; // Earth radius in meters
    double lat1 = Math.toRadians(latitude);
    double lon1 = Math.toRadians(longitude);
    double lat2 = Math.toRadians(vesselLatitude);
    double lon2 = Math.toRadians(vesselLongitude);

    double deltaLat = lat2 - lat1;
    double deltaLon = lon2 - lon1;

    double a = Math.pow(Math.sin(deltaLat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.pow(Math.sin(deltaLon / 2), 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }

  private void validateDeletion(Long notificationId, Long userId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    if (!notification.getRegisteredUser().getId().equals(userId)) {
      log.error("User with id {} tried to delete notification with id {} that does not belong to them",
          userId, notificationId);
      throw new RuntimeException(NOTIFICATION_S_DOES_NOT_BELONG_TO_THE_USER_WITH_ID_S.formatted(notificationId, userId));
    }
  }
}