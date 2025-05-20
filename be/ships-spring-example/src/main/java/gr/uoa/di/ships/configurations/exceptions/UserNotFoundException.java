package gr.uoa.di.ships.configurations.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("User with id " + id + " not found");
  }
}
