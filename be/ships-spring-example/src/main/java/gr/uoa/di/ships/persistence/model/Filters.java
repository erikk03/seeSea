package gr.uoa.di.ships.persistence.model;

import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "filters")
public class Filters {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(mappedBy = "filters")
  private RegisteredUser registeredUser;

  @Column(name = "filter_from")
  private String filterFrom;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "filters_vessel_type",
      joinColumns = {@JoinColumn(name = "filters_id")},
      inverseJoinColumns = {@JoinColumn(name = "vessel_type_id")}
  )
  private List<VesselType> vesselTypes;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "filters_vessel_status",
      joinColumns = {@JoinColumn(name = "filters_id")},
      inverseJoinColumns = {@JoinColumn(name = "vessel_status_id")}
  )
  private List<VesselStatus> vesselStatuses;
}
