package gr.uoa.di.ships.configurations.exceptions;

public class VesselTypeNotFoundException extends RuntimeException {
  public VesselTypeNotFoundException(String description) {
        super("Vessel type \"" + description + "\" not found");
    }
}
