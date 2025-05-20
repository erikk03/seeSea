package gr.uoa.di.ships.persistence.repository.vessel;

import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselTypeRepository extends JpaRepository<VesselType, Long> {
  Optional<VesselType> findVesselTypeByName(String name);

  List<VesselType> findVesselTypesByIdIn(List<Long> ids);
}
