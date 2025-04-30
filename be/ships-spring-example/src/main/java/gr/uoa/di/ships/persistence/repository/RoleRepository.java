package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Role;
import gr.uoa.di.ships.persistence.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> getRoleByName(String name);
}