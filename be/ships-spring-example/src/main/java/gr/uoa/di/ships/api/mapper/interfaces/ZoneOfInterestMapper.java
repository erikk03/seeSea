package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;

public interface ZoneOfInterestMapper {
  GetZoneOfInterestDTO toGetZoneOfInterestDTO(ZoneOfInterest zoneOfInterest);
}
