package gr.uoa.di.ships.persistence.model.vessel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "vessel_history_data")
public class VesselHistoryData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_mmsi")
  private Vessel vessel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_status_id")
  private VesselStatus vesselStatus;

  @Column(name = "turn")
  private Float turn;

  @Column(name = "speed")
  private Float speed;

  @Column(name = "course")
  private Float course;

  @Column(name = "heading")
  private Integer heading;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "timestamp")
  private Long timestamp;

  @Column(name = "datetime_created")
  private LocalDateTime datetimeCreated;
}
