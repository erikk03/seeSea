package gr.uoa.di.ships.configurations.exceptions;

public class VesselTypeNotFoundException extends RuntimeException {
    public VesselTypeNotFoundException(String type) {
        super("Vessel type" + type + " not found");
    }
}
