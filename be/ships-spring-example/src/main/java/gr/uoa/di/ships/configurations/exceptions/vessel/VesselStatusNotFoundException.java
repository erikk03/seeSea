package gr.uoa.di.ships.configurations.exceptions.vessel;

public class VesselStatusNotFoundException extends RuntimeException {
  public VesselStatusNotFoundException(String description) {
    super("Vessel status [" + description + "] not found");
  }
}
