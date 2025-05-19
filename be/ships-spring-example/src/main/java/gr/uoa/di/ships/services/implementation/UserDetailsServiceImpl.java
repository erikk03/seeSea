package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.repository.RegisteredUserRepository;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class UserDetailsServiceImpl implements SeeSeaUserDetailsService {

  public static final String USER_NOT_FOUND = "User not found: %s";
  private final RegisteredUserRepository registeredUserRepository;

  public UserDetailsServiceImpl(RegisteredUserRepository registeredUserRepository) {
    this.registeredUserRepository = registeredUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return registeredUserRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.formatted(username)));
  }

  @Override
  public RegisteredUser getUserDetails() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(principal instanceof UserDetails userDetails)) {
      return null;
    }
    return (RegisteredUser) userDetails;
  }
}