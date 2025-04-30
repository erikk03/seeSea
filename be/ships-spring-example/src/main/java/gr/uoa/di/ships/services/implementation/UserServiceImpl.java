package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.configurations.security.JwtService;
import gr.uoa.di.ships.configurations.security.SecurityConfig;
import gr.uoa.di.ships.persistence.model.User;
import gr.uoa.di.ships.persistence.model.enums.RoleEnum;
import gr.uoa.di.ships.persistence.repository.UserRepository;
import gr.uoa.di.ships.services.interfaces.RoleService;
import gr.uoa.di.ships.services.interfaces.UserService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class UserServiceImpl implements UserService {

  private static final String ACCOUNT_WITH_THAT_EMAIL = "There is already a user with the email: ";
  private static final String INCORRECT_EMAIL_OR_PASSWORD = "Incorrect email or password";

  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final UserRepository userRepository;
  private final SecurityConfig securityConfig;
  private final RoleService roleService;

  public UserServiceImpl(JwtService jwtService,
                         AuthenticationManager authManager,
                         UserRepository userRepository,
                         SecurityConfig securityConfig,
                         RoleService roleService) {
    this.jwtService = jwtService;
    this.authManager = authManager;
    this.userRepository = userRepository;
    this.securityConfig = securityConfig;
    this.roleService = roleService;
  }

  @Override
  public void register(UserRegisterDTO userRegisterDTO) {
    User user = new User();
    validate(userRegisterDTO);
    user.setUsername(getUsernameFromEmail(userRegisterDTO.getEmail()));
    user.setEmail(userRegisterDTO.getEmail());
    user.setPassword(securityConfig.encoder().encode(userRegisterDTO.getPassword()));
    user.setRole(roleService.getRoleByName(RoleEnum.REGISTERED_USER.name()));
    userRepository.save(user);
    log.info("Created user: {}", user.getUsername());
  }

  @Override
  public JwtTokenDTO verify(UserAuthDTO userAuthDTO) {
    try {
      String username = Objects.nonNull(userAuthDTO.getUsername()) ? userAuthDTO.getUsername() : getUsernameFromEmail(userAuthDTO.getEmail());
      Authentication authentication = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, userAuthDTO.getPassword()));
      if (authentication.isAuthenticated()) {
        log.info("User with email {} logged in successfully.", userAuthDTO.getEmail());
        return JwtTokenDTO.builder()
            .token(jwtService.generateToken(username))
            .build();
      }
    } catch (AuthenticationException e) {
      log.error("Authentication failed: {}", e.getMessage(), e);
      throw new RuntimeException(INCORRECT_EMAIL_OR_PASSWORD, e);
    }
    return null;
  }

  private void validate(UserRegisterDTO userRegisterDTO) {
    if (Objects.nonNull(userRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null))) {
      throw new RuntimeException(ACCOUNT_WITH_THAT_EMAIL + userRegisterDTO.getEmail());
    }
  }

  private String getUsernameFromEmail(String email) {
    String[] parts = email.split("@");
    if (parts.length > 0) {
      return parts[0];
    } else {
      throw new IllegalArgumentException("Invalid email format: " + email);
    }
  }
}