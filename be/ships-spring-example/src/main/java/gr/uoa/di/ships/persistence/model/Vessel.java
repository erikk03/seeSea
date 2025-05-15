package gr.uoa.di.ships.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vessel")
public class Vessel {

  @Id
  @Column(name = "mmsi", nullable = false, unique = true)
  private String mmsi;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_type_id")
  private VesselType vesselType;

  @OneToMany(mappedBy = "vessel")
  private Set<VesselHistoryData> vesselHistoryData;

  public Vessel(String mmsi) {
    this.mmsi = mmsi;
  }
}
