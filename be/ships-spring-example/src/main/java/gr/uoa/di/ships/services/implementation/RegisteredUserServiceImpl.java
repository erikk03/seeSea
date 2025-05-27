package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.api.mapper.interfaces.RegisteredUserMapper;
import gr.uoa.di.ships.configurations.exceptions.UserNotFoundException;
import gr.uoa.di.ships.configurations.security.JwtService;
import gr.uoa.di.ships.configurations.security.SecurityConfig;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.enums.RoleEnum;
import gr.uoa.di.ships.persistence.repository.RegisteredUserRepository;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.RoleService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
public class RegisteredUserServiceImpl implements RegisteredUserService {

  private static final String ACCOUNT_WITH_THAT_EMAIL = "There is already a user with the email: ";
  private static final String INCORRECT_EMAIL_OR_PASSWORD = "Incorrect email or password";

  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final RegisteredUserRepository registeredUserRepository;
  private final SecurityConfig securityConfig;
  private final RoleService roleService;
  private final RegisteredUserMapper registeredUserMapper;
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;

  public RegisteredUserServiceImpl(JwtService jwtService,
                                   AuthenticationManager authManager,
                                   RegisteredUserRepository registeredUserRepository,
                                   SecurityConfig securityConfig,
                                   RoleService roleService,
                                   RegisteredUserMapper registeredUserMapper,
                                   SeeSeaUserDetailsService seeSeaUserDetailsService) {
    this.jwtService = jwtService;
    this.authManager = authManager;
    this.registeredUserRepository = registeredUserRepository;
    this.securityConfig = securityConfig;
    this.roleService = roleService;
    this.registeredUserMapper = registeredUserMapper;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
  }

  @Override
  public void register(UserRegisterDTO userRegisterDTO) {
    RegisteredUser registeredUser = new RegisteredUser();
    validate(userRegisterDTO);
    registeredUser.setUsername(getUsernameFromEmail(userRegisterDTO.getEmail()));
    registeredUser.setEmail(userRegisterDTO.getEmail());
    registeredUser.setPassword(securityConfig.encoder().encode(userRegisterDTO.getPassword()));
    registeredUser.setRole(roleService.getRoleByName(RoleEnum.REGISTERED_USER.name()));
    registeredUserRepository.save(registeredUser);
    log.info("Created user: {}", registeredUser.getUsername());
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

  @Override
  public UserInfoDTO getUserInfo() {
    return registeredUserMapper.toUserInfoDTO(getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId()));
  }

  @Override
  public void changePassword(String newPassword) {
    RegisteredUser registeredUser = getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    registeredUser.setPassword(securityConfig.encoder().encode(newPassword));
    registeredUserRepository.save(registeredUser);
  }

  @Override
  public RegisteredUser getRegisteredUserById(Long id) {
    return registeredUserRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  public void updateRegisteredUser(RegisteredUser registeredUser) {
    registeredUserRepository.findById(registeredUser.getId())
        .orElseThrow(() -> new UserNotFoundException(registeredUser.getId()));
    registeredUserRepository.save(registeredUser);
  }

  @Override
  public List<Long> getAllUsersIds() {
    return registeredUserRepository.findAll()
        .stream()
        .map(RegisteredUser::getId)
        .collect(Collectors.toList());
  }

  @Override
  public void saveRegisteredUser(RegisteredUser registeredUser) {
    registeredUserRepository.save(registeredUser);
  }

  private void validate(UserRegisterDTO userRegisterDTO) {
    if (Objects.nonNull(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null))) {
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