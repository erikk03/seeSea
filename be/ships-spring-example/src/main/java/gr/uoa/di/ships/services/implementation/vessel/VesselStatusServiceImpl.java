package gr.uoa.di.ships.services.implementation.vessel;

import gr.uoa.di.ships.configurations.exceptions.vessel.VesselStatusNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.repository.vessel.VesselStatusRepository;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
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
  public List<VesselStatus> getVesselStatusesByIds(List<Long> vesselStatusIds) {
    return vesselStatusRepository.getVesselStatusesByIdIn(vesselStatusIds);
  }

  @Override
  public VesselStatus getVesselStatusById(Long status) {
    final Long unknownVesselStatusId = 15L;
    return vesselStatusRepository.findById(status)
        .orElse(vesselStatusRepository.findById(unknownVesselStatusId)
                    .orElseThrow(() -> new VesselStatusNotFoundException(WITH_ID_S.formatted(status))));
  }
}