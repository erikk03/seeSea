package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.configurations.security.SecurityConfig;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.Role;
import gr.uoa.di.ships.persistence.model.enums.RoleEnum;
import gr.uoa.di.ships.persistence.repository.RegisteredUserRepository;
import gr.uoa.di.ships.services.implementation.RegisteredUserServiceImpl;
import gr.uoa.di.ships.services.interfaces.RoleService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class RegisteredUserServiceImplTest {

  @Mock
  private RegisteredUserRepository registeredUserRepository;

  @Mock
  private SecurityConfig securityConfig;

  @Mock
  private RoleService roleService;

  @InjectMocks
  private RegisteredUserServiceImpl registeredUserService;

  @Test
  void register_throwsRuntimeException_AlreadyExistingUserWithEmail() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("testPassword")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.of(new RegisteredUser()));

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> registeredUserService.register(userRegisterDTO)
    );
  }

  @Test
  void register_throwsRuntimeException_invalidPassword_lessThanEightChars() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("123")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> registeredUserService.register(userRegisterDTO)
    );
  }

  @Test
  void register_throwsRuntimeException_invalidPassword_noCapitalLetter() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("12345678")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> registeredUserService.register(userRegisterDTO)
    );
  }

  @Test
  void register_throwsRuntimeException_invalidPassword_noNumber() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("abcdefgH")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> registeredUserService.register(userRegisterDTO)
    );
  }

  @Test
  void register_throwsRuntimeException_invalidPassword_noSymbol() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("abcdefgH1")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> registeredUserService.register(userRegisterDTO)
    );
  }

  @Test
  void register_validPassword() {
    // Prepare
    UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
        .email("tempEmail")
        .password("abcdefgH1!")
        .build();

    when(registeredUserRepository.findByEmail(userRegisterDTO.getEmail()))
        .thenReturn(Optional.empty());
    when(securityConfig.encoder()).thenReturn(new BCryptPasswordEncoder());
    when(roleService.getRoleByName(RoleEnum.REGISTERED_USER.name())).thenReturn(new Role());

    // Execute
    registeredUserService.register(userRegisterDTO);

    // Verify
    verify(registeredUserRepository, times(1)).save(any(RegisteredUser.class));
  }

}
