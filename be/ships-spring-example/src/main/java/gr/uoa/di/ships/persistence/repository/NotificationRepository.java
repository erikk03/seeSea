package gr.uoa.di.ships.persistence.repository;

import gr.uoa.di.ships.persistence.model.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findAllByRegisteredUser_Id(Long registeredUserId);
}