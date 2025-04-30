package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.persistence.repository.UserRepository;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class UserDetailsServiceImpl implements SeeSeaUserDetailsService {

  public static final String USER_NOT_FOUND = "User not found: %s";
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.formatted(username)));
  }
}