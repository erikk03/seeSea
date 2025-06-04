package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.ChangePasswordDTO;
import gr.uoa.di.ships.api.dto.ChangeUsernameDTO;
import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import java.util.List;

public interface RegisteredUserService {
  void register(UserRegisterDTO userDTO);

  JwtTokenDTO verify(UserAuthDTO userAuthDTO);

  UserInfoDTO getUserInfo();

  void changePassword(ChangePasswordDTO changePasswordDTO);

  JwtTokenDTO changeUsername(ChangeUsernameDTO changeUsernameDTO);

  RegisteredUser getRegisteredUserById(Long id);

  void updateRegisteredUser(RegisteredUser registeredUser);

  List<Long> getAllUsersIds();

  void saveRegisteredUser(RegisteredUser registeredUser);

  void deleteRegisteredUser(String password);
}
