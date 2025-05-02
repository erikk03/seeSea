package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;

public interface RegisteredUserService {
  void register(UserRegisterDTO userDTO);

  JwtTokenDTO verify(UserAuthDTO userAuthDTO);
}
