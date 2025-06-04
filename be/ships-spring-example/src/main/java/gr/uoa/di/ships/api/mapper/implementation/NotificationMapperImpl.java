package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.api.mapper.interfaces.NotificationMapper;
import gr.uoa.di.ships.persistence.model.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class NotificationMapperImpl implements NotificationMapper {

  @Override
  public NotificationDTO toNotificationDTO(Notification notification) {
    return NotificationDTO.builder()
        .id(notification.getId())
        .description(notification.getDescription())
        .datetimeCreated(notification.getDatetimeCreated())
        .build();
  }
}
