package gr.uoa.di.ships.services.implementation.vessel;

import gr.uoa.di.ships.configurations.exceptions.vessel.VesselTypeNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.vessel.VesselTypeRepository;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  public List<VesselType> findAllVesselTypes() {
    return vesselTypeRepository.findAll();
  }

  @Override
  public List<VesselType> findVesselTypesByIds(List<Long> ids) {
    return vesselTypeRepository.findVesselTypesByIdIn(ids);
  }

  @Override
  public VesselType saveVesselType(VesselType vesselType) {
    return vesselTypeRepository.save(vesselType);
  }
}