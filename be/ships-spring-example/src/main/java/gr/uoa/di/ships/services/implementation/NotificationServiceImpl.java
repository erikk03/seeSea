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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

  public NotificationServiceImpl(NotificationRepository notificationRepository,
                                 SeeSeaUserDetailsService seeSeaUserDetailsService,
                                 NotificationMapper notificationMapper,
                                 RegisteredUserService registeredUserService) {
    this.notificationRepository = notificationRepository;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.notificationMapper = notificationMapper;
    this.registeredUserService = registeredUserService;
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
    double currentDistance = getHaversineDistance(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
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
    double currentDistance = getHaversineDistance(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    double radius = user.getZoneOfInterest().getRadius();
    if (Objects.isNull(previousVesselData)) {
      return currentDistance <= radius;
    } else {
      double previousDistance = getHaversineDistance(user, previousVesselData.getLatitude(), previousVesselData.getLongitude());
      return previousDistance > radius && currentDistance <= radius;
    }
  }

  @Override
  public boolean exitsZone(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    if (!user.getZoneOfInterestOptions().isExitsZone()) {
      return false;
    }
    double currentDistance = getHaversineDistance(user, jsonNodeToBeSent.get("lat").asDouble(), jsonNodeToBeSent.get("lon").asDouble());
    double radius = user.getZoneOfInterest().getRadius();
    if (Objects.isNull(previousVesselData)) {
      return false;
    } else {
      double previousDistance = getHaversineDistance(user, previousVesselData.getLatitude(), previousVesselData.getLongitude());
      return previousDistance <= radius && currentDistance > radius;
    }
  }

  private static double getHaversineDistance(RegisteredUser user, double vesselLatitude, double vesselLongitude) {
    double R = 6371000; // Earth radius in meters
    double lat1 = Math.toRadians(user.getZoneOfInterest().getCenterPointLatitude());
    double lon1 = Math.toRadians(user.getZoneOfInterest().getCenterPointLongitude());
    double lat2 = Math.toRadians(vesselLatitude);
    double lon2 = Math.toRadians(vesselLongitude);

    double deltaLat = lat2 - lat1;
    double deltaLon = lon2 - lon1;

    double a = Math.pow(Math.sin(deltaLat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.pow(Math.sin(deltaLon / 2), 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // distance in meters
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