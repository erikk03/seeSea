package gr.uoa.di.ships.persistence.repository.vessel;

import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselRepository extends JpaRepository<Vessel, Long> {
  Optional<Vessel> findByMmsi(String mmsi);
}
