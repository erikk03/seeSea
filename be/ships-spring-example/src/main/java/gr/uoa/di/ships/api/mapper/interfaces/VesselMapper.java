package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.VesselDTO;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;

public interface VesselMapper {
  VesselDTO toVesselDTO(Vessel vessel);
}
