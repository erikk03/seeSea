package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.ChangePasswordDTO;
import gr.uoa.di.ships.api.dto.ChangeUsernameDTO;
import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.api.mapper.interfaces.RegisteredUserMapper;
import gr.uoa.di.ships.configurations.exceptions.InvalidCredentialsException;
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
  private static final String YOU_CANNOT_DELETE_AN_ADMINISTRATOR_ACCOUNT = "You cannot delete an administrator account.";
  private static final String USER_WITH_EMAIL_S_DOES_NOT_EXIST = "User with email [%s] does not exist";

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
      String principal = Objects.nonNull(userAuthDTO.getUsername())
          ? userAuthDTO.getUsername()
          : registeredUserRepository.findByEmail(userAuthDTO.getEmail())
          .orElseThrow(() -> new InvalidCredentialsException(USER_WITH_EMAIL_S_DOES_NOT_EXIST.formatted(userAuthDTO.getEmail())))
          .getUsername();
      Authentication authentication = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(principal, userAuthDTO.getPassword()));
      if (authentication.isAuthenticated()) {
        return JwtTokenDTO.builder()
            .token(jwtService.generateToken(principal))
            .build();
      }
    } catch (AuthenticationException e) {
      throw new InvalidCredentialsException(INCORRECT_EMAIL_OR_PASSWORD, e);
    }
    throw new InvalidCredentialsException(INCORRECT_EMAIL_OR_PASSWORD);
  }

  @Override
  public UserInfoDTO getUserInfo() {
    return registeredUserMapper.toUserInfoDTO(getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId()));
  }

  @Override
  public void changePassword(ChangePasswordDTO changePasswordDTO) {
    RegisteredUser registeredUser = getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    verify(UserAuthDTO.builder()
               .email(registeredUser.getEmail())
               .password(changePasswordDTO.getOldPassword())
               .username(registeredUser.getUsername())
               .build());
    validatePassword(changePasswordDTO.getNewPassword());
    registeredUser.setPassword(securityConfig.encoder().encode(changePasswordDTO.getNewPassword()));
    registeredUserRepository.save(registeredUser);
    log.info("Password changed successfully for user: {}", registeredUser.getUsername());
  }

  @Override
  public JwtTokenDTO changeUsername(ChangeUsernameDTO changeUsernameDTO) {
    RegisteredUser registeredUser = getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    registeredUser.setUsername(changeUsernameDTO.getUsername());
    registeredUserRepository.save(registeredUser);
    log.info("Username changed successfully for user: {}", registeredUser.getEmail());
    return verify(UserAuthDTO.builder()
                      .email(registeredUser.getEmail())
                      .username(changeUsernameDTO.getUsername())
                      .password(changeUsernameDTO.getPassword())
                      .build());
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

  @Override
  public void deleteRegisteredUser(String password) {
    RegisteredUser user = getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    if (user.getRole().getName().equals(RoleEnum.ADMINISTRATOR.name())) {
      throw new RuntimeException(YOU_CANNOT_DELETE_AN_ADMINISTRATOR_ACCOUNT);
    }
    verify(UserAuthDTO.builder()
               .email(user.getEmail())
               .username(user.getUsername())
               .password(password)
               .build());
    deleteRegisteredUser(user);
  }

  private void deleteRegisteredUser(RegisteredUser registeredUser) {
    Long filtersId = Objects.nonNull(registeredUser.getFilters()) ? registeredUser.getFilters().getId() : null;
    Long zoneOfInterestId = Objects.nonNull(registeredUser.getZoneOfInterest()) ? registeredUser.getZoneOfInterest().getId() : null;
    Long zoneOfInterestOptionsId = Objects.nonNull(registeredUser.getZoneOfInterestOptions()) ? registeredUser.getZoneOfInterestOptions().getId() : null;
    String email = registeredUser.getEmail();
    registeredUserRepository.deleteRegisteredUserVessels(registeredUser.getId());
    registeredUserRepository.deleteRegisteredUserNotifications(registeredUser.getId());
    deleteFilterTypesAndStatuses(filtersId);
    registeredUserRepository.delete(registeredUser);
    unlinkForeignKeys(registeredUser, zoneOfInterestOptionsId, zoneOfInterestId, filtersId);
    registeredUserRepository.deleteRegisteredUserFilters(Objects.nonNull(filtersId) ? filtersId : null);
    registeredUserRepository.deleteRegisteredUserZoneOfInterest(Objects.nonNull(zoneOfInterestId) ? zoneOfInterestId : null);
    registeredUserRepository.deleteRegisteredUserZoneOfInterestOptions(Objects.nonNull(zoneOfInterestOptionsId) ? zoneOfInterestOptionsId : null);
    log.info("Deleted user with email: {}", email);
  }

  private static void unlinkForeignKeys(RegisteredUser registeredUser, Long zoneOfInterestOptionsId, Long zoneOfInterestId, final Long filtersId) {
    if (Objects.nonNull(filtersId)) {
      registeredUser.getFilters().setRegisteredUser(null);
      registeredUser.setFilters(null);
    }
    if (Objects.nonNull(zoneOfInterestId)) {
      registeredUser.getZoneOfInterest().setRegisteredUser(null);
      registeredUser.setZoneOfInterest(null);
    }
    if (Objects.nonNull(zoneOfInterestOptionsId)) {
      registeredUser.getZoneOfInterestOptions().setRegisteredUser(null);
      registeredUser.setZoneOfInterestOptions(null);
    }
  }

  private void deleteFilterTypesAndStatuses(Long filtersId) {
    if (Objects.nonNull(filtersId)) {
      registeredUserRepository.deleteRegisteredUserFiltersVesselTypes(filtersId);
      registeredUserRepository.deleteRegisteredUserFiltersVesselStatuses(filtersId);
    }
  }

  private void validate(UserRegisterDTO userRegisterDTO) {
    if (Objects.nonNull(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null))) {
      throw new RuntimeException(ACCOUNT_WITH_THAT_EMAIL + userRegisterDTO.getEmail());
    }
    validatePassword(userRegisterDTO.getPassword());
  }

  private static void validatePassword(String password) {
    if (password.length() < 8) {
      throw new RuntimeException("Password must be at least 8 characters long");
    }
    if (!password.matches(".*[A-Z].*")) {
      throw new RuntimeException("Password must contain at least one capital letter");
    }
    if (!password.matches(".*\\d.*")) {
      throw new RuntimeException("Password must contain at least one number");
    }
    if (!password.matches(".*[!@#$%^&*(),.?:{}|<>].*")) {
      throw new RuntimeException("Password must contain at least one of the following symbols: !@#$%^&*(),.?:{}|<>");
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