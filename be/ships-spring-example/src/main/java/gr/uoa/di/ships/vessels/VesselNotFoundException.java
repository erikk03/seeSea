package gr.uoa.di.ships.vessels;

public class VesselNotFoundException extends RuntimeException {
    public VesselNotFoundException(String mmsi) {
        super("Vessel " + mmsi + " not found");
    }
}
