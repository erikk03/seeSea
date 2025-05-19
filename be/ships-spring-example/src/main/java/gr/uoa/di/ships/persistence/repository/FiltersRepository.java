package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Filters;
import gr.uoa.di.ships.persistence.model.VesselHistoryData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FiltersRepository extends JpaRepository<Filters, Long>{
  Filters findByRegisteredUserId(Long registeredUserId);

  @Modifying
  @Query(value = """
      SELECT vhd.*
      FROM vessel_history_data vhd
      JOIN (
        SELECT vessel_mmsi, MAX(timestamp) AS latest_timestamp
        FROM vessel_history_data
        GROUP BY vessel_mmsi
      ) latest
        ON vhd.vessel_mmsi = latest.vessel_mmsi AND vhd.timestamp = latest.latest_timestamp
      JOIN vessel v ON v.mmsi = vhd.vessel_mmsi
      WHERE v.vessel_type_id in (:vesselTypeIds)
      AND vhd.vessel_status_id in (:vesselStatusIds)""",
      nativeQuery = true)
  List<VesselHistoryData> getVesselHistoryDataFiltered(@Param("vesselTypeIds") List<Long> vesselTypeIds,
                                                       @Param("vesselStatusIds") List<Long> vesselStatusIds);

  @Modifying
  @Query(value = """
      SELECT vhd.*
      FROM vessel_history_data vhd
      JOIN (
        SELECT vessel_mmsi, MAX(timestamp) AS latest_timestamp
        FROM vessel_history_data
        GROUP BY vessel_mmsi
      ) latest
        ON vhd.vessel_mmsi = latest.vessel_mmsi AND vhd.timestamp = latest.latest_timestamp
      JOIN vessel v ON v.mmsi = vhd.vessel_mmsi
      WHERE v.vessel_type_id in (:vesselTypeIds)
      AND vhd.vessel_status_id in (:vesselStatusIds)
      AND v.mmsi in (:mmsisFromFleet)""",
      nativeQuery = true)
  List<VesselHistoryData> getVesselHistoryDataFilteredByFleet(@Param("vesselTypeIds") List<Long> vesselTypeIds,
                                                              @Param("vesselStatusIds") List<Long> vesselStatusIds,
                                                              @Param("mmsisFromFleet") List<String> mmsisFromFleet);
}
