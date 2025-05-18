package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.VesselStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselStatusRepository extends JpaRepository<VesselStatus, Long>{
  List<VesselStatus> findVesselStatusesByIdIn(List<Long> ids);
}
