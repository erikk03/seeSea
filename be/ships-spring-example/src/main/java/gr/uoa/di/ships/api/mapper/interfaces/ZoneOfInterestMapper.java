package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.GetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;

public interface ZoneOfInterestMapper {
  GetZoneOfInterestDTO toGetZoneOfInterestDTO(ZoneOfInterest zoneOfInterest);

  ZoneOfInterestOptions toZoneOfInterestOptions(SetZoneOfInterestOptionsDTO zoneOfInterestOptionsDTO);

  GetZoneOfInterestOptionsDTO toGetZoneOfInterestOptionsDTO(ZoneOfInterestOptions zoneOfInterestOptions);
}
