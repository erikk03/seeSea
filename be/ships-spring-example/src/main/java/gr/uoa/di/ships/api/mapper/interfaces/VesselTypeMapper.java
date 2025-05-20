package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;

public interface VesselTypeMapper {
  SelectOptionDTO toSelectOptionDTO(VesselType vesselType);
}
