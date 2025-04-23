package gr.uoa.di.ships.vessels;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Vessel {

    private @Id              // mmsi is primary key
    String mmsi; // mmsi
    private String type;     // shiptype

    public Vessel(String mmsi, String type) {
        this.mmsi = mmsi;
        this.type = type;
    }

    public Vessel() {

    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vessel vessel = (Vessel) o;
        return Objects.equals(this.mmsi, vessel.mmsi ) &&
                Objects.equals(this.type, vessel.type);
    }

    @Override
    public String toString() {
        return "Vessel{" +"mmsi='" + mmsi + '\'' + ", type='" + type + '\'' + '}';
    }
}
