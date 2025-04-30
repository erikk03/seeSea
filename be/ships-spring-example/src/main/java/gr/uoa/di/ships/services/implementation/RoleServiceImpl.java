package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.persistence.model.Role;
import gr.uoa.di.ships.persistence.repository.RoleRepository;
import gr.uoa.di.ships.services.interfaces.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class RoleServiceImpl implements RoleService {

  private static final String ROLE_NOT_FOUND = "Role not found: %s";
  private final RoleRepository roleRepository;

  public RoleServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public Role getRoleByName(String name) {
    return roleRepository.getRoleByName(name)
        .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND.formatted(name)));
  }
}