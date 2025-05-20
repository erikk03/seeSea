package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselStatusMapper;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class VesselStatusMapperImpl implements VesselStatusMapper {

  @Override
  public SelectOptionDTO toSelectOptionDTO(VesselStatus vesselStatus) {
    return SelectOptionDTO.builder()
        .id(vesselStatus.getId())
        .name(vesselStatus.getName())
        .build();
  }
}
