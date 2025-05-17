package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiltersRepository extends JpaRepository<Filters, Long>{
    Filters findByRegisteredUserId(Long registeredUserId);
}
