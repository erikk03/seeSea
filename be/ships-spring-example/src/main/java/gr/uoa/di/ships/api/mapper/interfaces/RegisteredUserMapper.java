package gr.uoa.di.ships.api.mapper.interfaces;

import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.persistence.model.RegisteredUser;

public interface RegisteredUserMapper {
  UserInfoDTO toUserInfoDTO(RegisteredUser registeredUser);
}
