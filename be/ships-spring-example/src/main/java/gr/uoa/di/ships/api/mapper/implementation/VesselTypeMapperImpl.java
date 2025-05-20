package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselTypeMapper;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class VesselTypeMapperImpl implements VesselTypeMapper {

  @Override
  public SelectOptionDTO toSelectOptionDTO(VesselType vesselType) {
    return SelectOptionDTO.builder()
        .id(vesselType.getId())
        .name(vesselType.getName())
        .build();
  }
}
