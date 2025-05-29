package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneOfInterestOptionsRepository extends JpaRepository<ZoneOfInterestOptions, Long> {
}