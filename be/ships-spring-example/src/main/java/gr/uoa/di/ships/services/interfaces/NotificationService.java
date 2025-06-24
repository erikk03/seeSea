package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.util.List;

public interface NotificationService {
  List<NotificationDTO> getAllNotifications();

  void deleteNotification(Long id);

  void saveNotification(String description, RegisteredUser user);

  boolean violatesMaxSpeed(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData);

  boolean entersZone(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData);

  boolean exitsZone(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData);
}
