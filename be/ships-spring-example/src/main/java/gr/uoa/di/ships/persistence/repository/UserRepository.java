package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @EntityGraph(attributePaths = "role")
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}