package gr.uoa.di.ships.configurations.exceptions;

public class MigrationNotFoundException extends RuntimeException {
  public MigrationNotFoundException(String description) {
    super("Migration [" + description + "] not found");
  }
}
