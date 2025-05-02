package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.Role;

public interface RoleService {
  Role getRoleByName(String name);
}
