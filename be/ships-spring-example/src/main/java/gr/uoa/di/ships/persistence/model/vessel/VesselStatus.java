package gr.uoa.di.ships.persistence.model.vessel;

import gr.uoa.di.ships.persistence.model.Filters;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "vessel_status")
public class VesselStatus {

  @Id
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "vesselStatuses", fetch = FetchType.EAGER)
  private List<Filters> filters;

  @OneToMany(mappedBy = "vesselStatus")
  private Set<VesselHistoryData> vesselHistoryData;
}
