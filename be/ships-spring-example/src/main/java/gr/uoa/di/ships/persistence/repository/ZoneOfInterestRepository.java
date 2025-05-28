package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneOfInterestRepository extends JpaRepository<ZoneOfInterest, Long> {
}