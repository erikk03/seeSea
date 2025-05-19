package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SeeSeaUserDetailsService extends UserDetailsService {
  RegisteredUser getUserDetails();
}
