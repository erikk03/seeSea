package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.persistence.model.Role;
import javax.net.ssl.SSLSession;

public interface RoleService {

  Role getRoleByName(String name);
}
