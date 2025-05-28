package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
  List<NotificationDTO> getAllNotifications();

  void deleteNotification(Long id);
}
