package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
  @EntityGraph(attributePaths = "role")
  Optional<RegisteredUser> findByUsername(String username);

  Optional<RegisteredUser> findByEmail(String email);

  @Modifying
  @Query(value = """
      DELETE FROM registered_user_vessel
      WHERE registered_user_id = :registeredUserId""",
      nativeQuery = true)
  void deleteRegisteredUserVessels(Long registeredUserId);

  @Modifying
  @Query(value = """
      DELETE FROM filters_vessel_type
      WHERE filters_id = :filtersId""",
      nativeQuery = true)
  void deleteRegisteredUserFiltersVesselTypes(Long filtersId);

  @Modifying
  @Query(value = """
      DELETE FROM filters_vessel_status
      WHERE filters_id = :filtersId""",
      nativeQuery = true)
  void deleteRegisteredUserFiltersVesselStatuses(Long filtersId);

  @Modifying
  @Query(value = """
      DELETE FROM filters
      WHERE id = :filtersId""",
      nativeQuery = true)
  void deleteRegisteredUserFilters(Long filtersId);

  @Modifying
  @Query(value = """
      DELETE FROM notification
      WHERE registered_user_id = :registeredUserId""",
      nativeQuery = true)
  void deleteRegisteredUserNotifications(Long registeredUserId);

  @Modifying
  @Query(value = """
      DELETE FROM zone_of_interest
      WHERE id = :zoneOfInterestId""",
      nativeQuery = true)
  void deleteRegisteredUserZoneOfInterest(Long zoneOfInterestId);

  @Modifying
  @Query(value = """
      DELETE FROM zone_of_interest_options
      WHERE id = :zoneOfInterestOptionsId""",
      nativeQuery = true)
  void deleteRegisteredUserZoneOfInterestOptions(Long zoneOfInterestOptionsId);
}