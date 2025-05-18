package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.VesselStatus;
import java.util.List;

public interface VesselStatusService {
  List<VesselStatus> findAllVesselStatuses();

  VesselStatus saveVesselStatus(VesselStatus vesselStatus);

  List<VesselStatus> getVesselStatusesByIds(List<Long> vesselStatusIds);
}
