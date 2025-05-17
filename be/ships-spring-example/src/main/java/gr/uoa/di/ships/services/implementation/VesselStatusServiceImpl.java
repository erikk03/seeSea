package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.configurations.exceptions.VesselStatusNotFoundException;
import gr.uoa.di.ships.persistence.model.VesselStatus;
import gr.uoa.di.ships.persistence.repository.VesselStatusRepository;
import gr.uoa.di.ships.services.interfaces.VesselStatusService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class VesselStatusServiceImpl implements VesselStatusService {

  private static final String WITH_ID_S = "with id %s";
  private final VesselStatusRepository vesselStatusRepository;

  public VesselStatusServiceImpl(VesselStatusRepository vesselStatusRepository) {
    this.vesselStatusRepository = vesselStatusRepository;
  }

  @Override
  public List<VesselStatus> findAllVesselStatuses() {
    return vesselStatusRepository.findAll();
  }

  @Override
  public VesselStatus saveVesselStatus(VesselStatus vesselStatus) {
    return vesselStatusRepository.save(vesselStatus);
  }

  @Override
  public VesselStatus findVesselStatusById(Long vesselStatusId) {
    return vesselStatusRepository.findById(vesselStatusId)
        .orElseThrow(() -> new VesselStatusNotFoundException(WITH_ID_S.formatted(vesselStatusId)));
  }
}