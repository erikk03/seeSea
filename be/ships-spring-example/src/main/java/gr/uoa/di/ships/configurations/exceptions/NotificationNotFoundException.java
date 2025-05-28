package gr.uoa.di.ships.configurations.exceptions;

public class NotificationNotFoundException extends RuntimeException {
  public NotificationNotFoundException(Long id) {
    super("Notification with id " + id + " not found");
  }
}
