package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FiltersRepository extends JpaRepository<Filters, Long> {
  Filters findByRegisteredUserId(Long registeredUserId);

  @Modifying
  @Query(value = """
      SELECT vhd.*
      FROM vessel_history_data vhd
      JOIN (
          SELECT vessel_mmsi, MAX(timestamp) AS max_timestamp
          FROM vessel_history_data
          GROUP BY vessel_mmsi
      ) t1 ON vhd.vessel_mmsi = t1.vessel_mmsi AND vhd.timestamp = t1.max_timestamp
      JOIN (
          SELECT vessel_mmsi, timestamp, MAX(datetime_created) AS max_created
          FROM vessel_history_data
          GROUP BY vessel_mmsi, timestamp
      ) t2 ON vhd.vessel_mmsi = t2.vessel_mmsi AND vhd.timestamp = t2.timestamp AND vhd.datetime_created = t2.max_created
      JOIN vessel v ON v.mmsi = vhd.vessel_mmsi
      WHERE v.vessel_type_id in (:vesselTypeIds)
      AND vhd.vessel_status_id in (:vesselStatusIds)""",
      nativeQuery = true)
  List<VesselHistoryData> getVesselHistoryDataFiltered(@Param("vesselTypeIds") List<Long> vesselTypeIds,
                                                       @Param("vesselStatusIds") List<Long> vesselStatusIds);
}
