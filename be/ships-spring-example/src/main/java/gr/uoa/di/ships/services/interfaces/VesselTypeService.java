package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.VesselType;
import java.util.List;

public interface VesselTypeService {
  VesselType findVesselTypeByName(String name);

  List<VesselType> findAllVesselTypes();

  VesselType saveVesselType(VesselType vesselType);

  VesselType findVesselTypeById(Long id);
}
