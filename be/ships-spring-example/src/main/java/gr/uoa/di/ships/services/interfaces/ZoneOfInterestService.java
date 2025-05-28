package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestDTO;

public interface ZoneOfInterestService {
  GetZoneOfInterestDTO getZoneOfInterest();

  void setZoneOfInterest(SetZoneOfInterestDTO setZoneOfInterestDTO);

  void deleteZoneOfInterest(Long id);
}
