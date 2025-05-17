package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.VesselStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselStatusRepository extends JpaRepository<VesselStatus, Long>{
}
