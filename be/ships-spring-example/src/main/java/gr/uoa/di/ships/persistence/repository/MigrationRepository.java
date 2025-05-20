package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Migration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationRepository extends JpaRepository<Migration, Long> {
  Optional<Migration> findByDescription(String description);
}
