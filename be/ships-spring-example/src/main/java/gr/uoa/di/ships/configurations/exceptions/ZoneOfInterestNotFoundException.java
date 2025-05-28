package gr.uoa.di.ships.configurations.exceptions;

public class ZoneOfInterestNotFoundException extends RuntimeException {
  public ZoneOfInterestNotFoundException(Long id) {
    super("Zone of Interest with id " + id + " not found");
  }
}
