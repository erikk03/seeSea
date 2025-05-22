package gr.uoa.di.ships.services.interfaces.vessel;

import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import java.util.List;

public interface VesselTypeService {
  VesselType findVesselTypeByName(String name);

  List<VesselType> findAllVesselTypes();

  List<VesselType> findVesselTypesByIds(List<Long> ids);

  VesselType saveVesselType(VesselType vesselType);

  List<SelectOptionDTO> getAllVesselTypes();
}
