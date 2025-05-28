package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.NotificationDTO;
import gr.uoa.di.ships.services.interfaces.NotificationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationsController {

  private final NotificationService notificationService;

  public NotificationsController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/get-all-notifications")
  @ResponseStatus(HttpStatus.OK)
  public List<NotificationDTO> getNotifications() {
    return notificationService.getAllNotifications();
  }

  @DeleteMapping("/delete-notification")
  @ResponseStatus(HttpStatus.OK)
  public void deleteNotification(@RequestParam Long id) {
    notificationService.deleteNotification(id);
  }
}
