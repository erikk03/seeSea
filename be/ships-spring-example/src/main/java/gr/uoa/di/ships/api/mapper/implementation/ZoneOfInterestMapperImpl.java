package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.mapper.interfaces.ZoneOfInterestMapper;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class ZoneOfInterestMapperImpl implements ZoneOfInterestMapper {

  @Override
  public GetZoneOfInterestDTO toGetZoneOfInterestDTO(ZoneOfInterest zoneOfInterest) {
    return GetZoneOfInterestDTO.builder()
        .id(Objects.nonNull(zoneOfInterest) ? zoneOfInterest.getId() : null)
        .radius(Objects.nonNull(zoneOfInterest) ? zoneOfInterest.getRadius() : null)
        .centerPointLatitude(Objects.nonNull(zoneOfInterest) ? zoneOfInterest.getCenterPointLatitude() : null)
        .centerPointLongitude(Objects.nonNull(zoneOfInterest) ? zoneOfInterest.getCenterPointLongitude() : null)
        .build();
  }
}
