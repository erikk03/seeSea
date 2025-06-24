package gr.uoa.di.ships.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.util.List;

public interface CollisionWarningService {
  List<String> collisionWarningWithVessels(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData);
}
