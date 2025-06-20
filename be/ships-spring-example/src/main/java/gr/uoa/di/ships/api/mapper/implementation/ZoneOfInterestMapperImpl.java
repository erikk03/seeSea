package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.GetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.mapper.interfaces.ZoneOfInterestMapper;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;
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

  @Override
  public ZoneOfInterestOptions toZoneOfInterestOptions(SetZoneOfInterestOptionsDTO setZoneOfInterestOptionsDTO) {
    return ZoneOfInterestOptions.builder()
        .maxSpeed(setZoneOfInterestOptionsDTO.getMaxSpeed())
        .entersZone(setZoneOfInterestOptionsDTO.isEntersZone())
        .exitsZone(setZoneOfInterestOptionsDTO.isExitsZone())
        .collisionMonitoring(setZoneOfInterestOptionsDTO.isCollisionMonitoring())
        .build();
  }

  @Override
  public GetZoneOfInterestOptionsDTO toGetZoneOfInterestOptionsDTO(ZoneOfInterestOptions zoneOfInterestOptions) {
    return GetZoneOfInterestOptionsDTO.builder()
        .id(Objects.nonNull(zoneOfInterestOptions) ? zoneOfInterestOptions.getId() : null)
        .maxSpeed(Objects.nonNull(zoneOfInterestOptions) ? zoneOfInterestOptions.getMaxSpeed() : null)
        .entersZone(Objects.nonNull(zoneOfInterestOptions) && zoneOfInterestOptions.isEntersZone())
        .exitsZone(Objects.nonNull(zoneOfInterestOptions) && zoneOfInterestOptions.isExitsZone())
        .collisionMonitoring(Objects.nonNull(zoneOfInterestOptions) && zoneOfInterestOptions.isCollisionMonitoring())
        .build();
  }
}
