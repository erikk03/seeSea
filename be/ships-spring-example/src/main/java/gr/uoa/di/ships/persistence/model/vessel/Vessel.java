package gr.uoa.di.ships.persistence.model.vessel;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "registered_user_vessel",
      joinColumns = {@JoinColumn(name = "vessel_mmsi")},
      inverseJoinColumns = {@JoinColumn(name = "registered_user_id")}
  )
  private Set<RegisteredUser> registeredUsers;

  public Vessel(String mmsi, VesselType vesselType) {
    this.mmsi = mmsi;
    this.vesselType = vesselType;
  }
}
