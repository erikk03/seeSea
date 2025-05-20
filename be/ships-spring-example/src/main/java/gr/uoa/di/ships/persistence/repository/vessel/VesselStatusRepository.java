package gr.uoa.di.ships.persistence.repository.vessel;

import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselStatusRepository extends JpaRepository<VesselStatus, Long> {
  List<VesselStatus> getVesselStatusesByIdIn(List<Long> ids);
}
