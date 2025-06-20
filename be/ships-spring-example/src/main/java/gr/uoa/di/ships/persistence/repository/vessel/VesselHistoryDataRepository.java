package gr.uoa.di.ships.persistence.repository.vessel;

import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselHistoryDataRepository extends JpaRepository<VesselHistoryData, Long> {

  void deleteByDatetimeCreatedBefore(LocalDateTime dateTime);

  @Query(value = """
      SELECT vhd.*
      FROM vessel_history_data vhd
      JOIN (
          SELECT vessel_mmsi, MAX(timestamp) AS max_timestamp
          FROM vessel_history_data
          GROUP BY vessel_mmsi
      ) t1 ON vhd.vessel_mmsi = t1.vessel_mmsi AND vhd.timestamp = t1.max_timestamp
      JOIN (
          SELECT vessel_mmsi, timestamp, MAX(datetime_created) AS max_datetime_created
          FROM vessel_history_data
          GROUP BY vessel_mmsi, timestamp
      ) t2 ON vhd.vessel_mmsi = t2.vessel_mmsi AND vhd.timestamp = t2.timestamp AND vhd.datetime_created = t2.max_datetime_created
      WHERE vhd.vessel_mmsi = :mmsi""",
      nativeQuery = true)
  Optional<VesselHistoryData> findLastVesselHistoryDataForMmsi(@Param("mmsi") String mmsi);

  @Query(value = """
      SELECT vhd.*
      FROM vessel_history_data vhd
      JOIN (
          SELECT vessel_mmsi, MAX(timestamp) AS max_timestamp
          FROM vessel_history_data
          GROUP BY vessel_mmsi
      ) t1 ON vhd.vessel_mmsi = t1.vessel_mmsi AND vhd.timestamp = t1.max_timestamp
      JOIN (
          SELECT vessel_mmsi, timestamp, MAX(datetime_created) AS max_datetime_created
          FROM vessel_history_data
          GROUP BY vessel_mmsi, timestamp
      ) t2 ON vhd.vessel_mmsi = t2.vessel_mmsi AND vhd.timestamp = t2.timestamp AND vhd.datetime_created = t2.max_datetime_created""",
      nativeQuery = true)
  List<VesselHistoryData> findLastVesselHistoryData();

  List<VesselHistoryData> findVesselHistoryDataByVessel_Mmsi(String mmsi);
}
