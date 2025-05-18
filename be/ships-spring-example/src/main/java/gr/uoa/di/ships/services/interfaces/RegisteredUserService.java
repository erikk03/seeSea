package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import java.util.List;

public interface RegisteredUserService {
  void register(UserRegisterDTO userDTO);

  JwtTokenDTO verify(UserAuthDTO userAuthDTO);

  UserInfoDTO getUserInfo(Long id);

  void changePassword(Long id, String newPassword);

  RegisteredUser getRegisteredUserById(Long id);

  List<Long> getAllUsersIds();
}
