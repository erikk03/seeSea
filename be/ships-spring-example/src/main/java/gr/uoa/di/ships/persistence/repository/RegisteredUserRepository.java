package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
  @EntityGraph(attributePaths = "role")
  Optional<RegisteredUser> findByUsername(String username);

  Optional<RegisteredUser> findByEmail(String email);
}