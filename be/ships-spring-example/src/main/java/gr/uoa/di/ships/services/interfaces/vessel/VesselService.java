package gr.uoa.di.ships.services.interfaces.vessel;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import java.util.List;
import java.util.Optional;

public interface VesselService {

  void updateVesselType(UpdateVesselDTO updateVesselDTO);

  List<Vessel> getAllVessels();

  void saveAllVessels(List<Vessel> vessels);

  Optional<Vessel> getVesselByMMSI(String mmsi);

  Vessel saveVessel(Vessel vessel);
}
