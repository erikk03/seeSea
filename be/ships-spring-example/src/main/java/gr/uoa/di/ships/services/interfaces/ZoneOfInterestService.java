package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.GetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestOptionsDTO;

public interface ZoneOfInterestService {
  GetZoneOfInterestDTO getZoneOfInterest();

  void setZoneOfInterest(SetZoneOfInterestDTO setZoneOfInterestDTO);

  void deleteZoneOfInterest(Long id);

  void setZoneOfInterestOptions(SetZoneOfInterestOptionsDTO setZoneOfInterestOptionsDTO);

  GetZoneOfInterestOptionsDTO getZoneOfInterestOptions();
}
