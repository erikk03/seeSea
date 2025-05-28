package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.persistence.model.Notification;

public interface NotificationMapper {
  NotificationDTO toNotificationDTO(Notification notification);
}
