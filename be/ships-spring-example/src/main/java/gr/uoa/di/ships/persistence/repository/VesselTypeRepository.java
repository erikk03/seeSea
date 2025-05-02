package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.VesselType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselTypeRepository extends JpaRepository<VesselType, Long>{
  Optional<VesselType> findVesselTypeByName(String name);
}
