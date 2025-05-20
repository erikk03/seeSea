package gr.uoa.di.ships.services.interfaces.vessel;

import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import java.util.List;

public interface VesselStatusService {
  List<VesselStatus> findAllVesselStatuses();

  VesselStatus saveVesselStatus(VesselStatus vesselStatus);

  List<VesselStatus> getVesselStatusesByIds(List<Long> vesselStatusIds);

  VesselStatus getVesselStatusById(Long status);
}
