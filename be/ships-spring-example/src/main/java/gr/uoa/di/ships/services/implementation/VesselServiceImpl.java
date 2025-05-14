package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.configurations.exceptions.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.repository.VesselRepository;
import gr.uoa.di.ships.services.interfaces.VesselService;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class VesselServiceImpl implements VesselService {

  private final VesselRepository vesselRepository;
  private final VesselTypeService vesselTypeService;

  public VesselServiceImpl(VesselRepository vesselRepository, VesselTypeService vesselTypeService) {
    this.vesselRepository = vesselRepository;
    this.vesselTypeService = vesselTypeService;
  }

  @Override
  public void updateVesselType(UpdateVesselDTO updateVesselDTO) {
    Vessel vessel = vesselRepository.findByMmsi(updateVesselDTO.getMmsi())
        .orElseThrow(() -> new VesselNotFoundException(updateVesselDTO.getMmsi()));
    vessel.setVesselType(vesselTypeService.findVesselTypeByName(updateVesselDTO.getNewType()));
  }

  @Override
  public List<Vessel> getAllVessels() {
    return vesselRepository.findAll();
  }

  @Override
  public void saveAllVessels(List<Vessel> vessels) {
    vesselRepository.saveAll(vessels);
  }
}