package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.configurations.exceptions.VesselNotFoundException;
import gr.uoa.di.ships.configurations.exceptions.VesselTypeNotFoundException;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.model.VesselType;
import gr.uoa.di.ships.persistence.repository.VesselRepository;
import gr.uoa.di.ships.persistence.repository.VesselTypeRepository;
import gr.uoa.di.ships.services.interfaces.VesselService;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class VesselTypeServiceImpl implements VesselTypeService {

  private final VesselTypeRepository vesselTypeRepository;

  public VesselTypeServiceImpl(VesselTypeRepository vesselTypeRepository) {
    this.vesselTypeRepository = vesselTypeRepository;
  }

  @Override
  public VesselType findVesselTypeByName(String name) {
    return vesselTypeRepository.findVesselTypeByName(name)
        .orElseThrow(() -> new VesselTypeNotFoundException(name));
  }
}