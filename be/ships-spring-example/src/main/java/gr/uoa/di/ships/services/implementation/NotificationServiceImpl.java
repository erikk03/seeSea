package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.api.mapper.interfaces.NotificationMapper;
import gr.uoa.di.ships.configurations.exceptions.NotificationNotFoundException;
import gr.uoa.di.ships.persistence.model.Notification;
import gr.uoa.di.ships.persistence.repository.NotificationRepository;
import gr.uoa.di.ships.services.interfaces.NotificationService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import java.util.List;
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

  public NotificationServiceImpl(NotificationRepository notificationRepository,
                                 SeeSeaUserDetailsService seeSeaUserDetailsService,
                                 NotificationMapper notificationMapper) {
    this.notificationRepository = notificationRepository;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.notificationMapper = notificationMapper;
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