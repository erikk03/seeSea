package gr.uoa.di.ships.api.mapper.implementation;

import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.api.mapper.interfaces.RegisteredUserMapper;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.enums.RoleEnum;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional()
public class RegisteredUserMapperImpl implements RegisteredUserMapper {

  @Override
  public UserInfoDTO toUserInfoDTO(RegisteredUser registeredUser) {
    return UserInfoDTO.builder()
        .id(registeredUser.getId())
        .username(registeredUser.getUsername())
        .email(registeredUser.getEmail())
        .role(RoleEnum.fromRole(registeredUser.getRole().getName()).getAuthority())
        .build();
  }
}
