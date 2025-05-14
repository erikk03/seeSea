package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.persistence.model.Vessel;
import java.util.List;

public interface VesselService {

  void updateVesselType(UpdateVesselDTO updateVesselDTO);

  List<Vessel> getAllVessels();

  void saveAllVessels(List<Vessel> vessels);
}
