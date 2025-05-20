package gr.uoa.di.ships.persistence.repository.vessel;

import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselHistoryDataRepository extends JpaRepository<VesselHistoryData, Long> {

  void deleteByDatetimeCreatedBefore(LocalDateTime dateTime);
}
