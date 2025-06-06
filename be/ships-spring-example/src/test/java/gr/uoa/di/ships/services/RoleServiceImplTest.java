package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.persistence.model.Role;
import gr.uoa.di.ships.persistence.repository.RoleRepository;
import gr.uoa.di.ships.services.implementation.RoleServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private RoleServiceImpl roleService;

  @Test
  void getRoleByName() {
    // Prepare
    String roleName = "Administrator";
    when(roleRepository.getRoleByName(roleName))
        .thenReturn(Optional.ofNullable(Role.builder().name(roleName).build()));

    // Execute
    Role result = roleService.getRoleByName(roleName);

    // Verify
    assertNotNull(result);
    assertEquals(roleName, result.getName());
  }

  @Test
  void getRoleByName_throwsIllegalArgumentException() {
    // Prepare
    String roleName = "Administrator";
    when(roleRepository.getRoleByName(roleName))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        IllegalArgumentException.class,
        () -> roleService.getRoleByName(roleName)
    );
  }
}
