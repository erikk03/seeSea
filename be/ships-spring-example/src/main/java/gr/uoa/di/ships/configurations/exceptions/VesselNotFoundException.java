package gr.uoa.di.ships.configurations.exceptions;

public class VesselNotFoundException extends RuntimeException {
    public VesselNotFoundException(String mmsi) {
        super("Vessel " + mmsi + " not found");
    }
}
