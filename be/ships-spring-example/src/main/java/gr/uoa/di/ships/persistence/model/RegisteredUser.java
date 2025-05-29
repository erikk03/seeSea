package gr.uoa.di.ships.persistence.model;

import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type_id", discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "registered_user")
@DiscriminatorValue("1")
public class RegisteredUser implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private Role role;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "registered_user_vessel",
      joinColumns = {@JoinColumn(name = "registered_user_id")},
      inverseJoinColumns = {@JoinColumn(name = "vessel_mmsi")}
  )
  private Set<Vessel> vessels;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "filters_id")
  private Filters filters;

  @OneToMany(mappedBy = "registeredUser")
  private Set<Notification> notifications;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "zone_of_interest_id")
  private ZoneOfInterest zoneOfInterest;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "zone_of_interest_options_id")
  private ZoneOfInterestOptions zoneOfInterestOptions;

  @Override
  public String toString() {
    return "Users{"
        + "id=" + id
        + ", username='" + username + '\''
        + ", password='" + password + '\''
        +  '}';
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(this.role.getName()));
  }
}
