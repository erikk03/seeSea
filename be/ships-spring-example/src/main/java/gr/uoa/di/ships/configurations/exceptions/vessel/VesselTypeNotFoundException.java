package gr.uoa.di.ships.configurations.exceptions.vessel;

public class VesselTypeNotFoundException extends RuntimeException {
  public VesselTypeNotFoundException(String description) {
    super("Vessel type [" + description + "] not found");
  }
}
