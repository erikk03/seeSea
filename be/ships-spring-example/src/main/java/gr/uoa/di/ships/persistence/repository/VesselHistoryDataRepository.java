package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselHistoryDataRepository extends JpaRepository<VesselHistoryData, Long>{

  void deleteByDatetimeCreatedBefore(LocalDateTime dateTime);

  @Modifying
  @Query(value = """
      SELECT v.*
      FROM vessel_history_data v
      JOIN (
        SELECT vessel_mmsi, MAX(timestamp) AS latest_timestamp
        FROM vessel_history_data
        GROUP BY vessel_mmsi
      ) latest
        ON v.vessel_mmsi = latest.vessel_mmsi AND v.timestamp = latest.latest_timestamp;""",
      nativeQuery = true)
  List<VesselHistoryData> getLastVesselHistoryDataPerVessel();
}
