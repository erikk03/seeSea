package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.repository.RegisteredUserRepository;
import gr.uoa.di.ships.services.implementation.UserDetailsServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

  @Mock
  private RegisteredUserRepository registeredUserRepository;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @Test
  void loadUserByUsername() {
    // Prepare
    String username = "testUser";
    when(registeredUserRepository.findByUsername(username))
        .thenReturn(Optional.of(RegisteredUser.builder().username(username).build()));

    // Execute
    UserDetails result = userDetailsService.loadUserByUsername(username);

    // Verify
    assertNotNull(result);
    assertEquals(username, result.getUsername());
  }

  @Test
  void loadUserByUsername_throwsUsernameNotFoundException() {
    // Prepare
    String username = "testUser";
    when(registeredUserRepository.findByUsername(username))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username)
    );
  }
}
