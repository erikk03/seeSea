package gr.uoa.di.ships.services.implementation.vessel;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.configurations.exceptions.vessel.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.repository.vessel.VesselRepository;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.List;
import java.util.Optional;
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

  @Override
  public Optional<Vessel> getVesselByMMSI(String mmsi) {
    return vesselRepository.findByMmsi(mmsi);
  }

  @Override
  public Vessel saveVessel(final Vessel vessel) {
    return vesselRepository.save(vessel);
  }
}