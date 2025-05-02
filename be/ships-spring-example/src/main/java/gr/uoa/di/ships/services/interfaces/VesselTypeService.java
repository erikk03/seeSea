package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.VesselType;

public interface VesselTypeService {
  VesselType findVesselTypeByName(String name);
}
