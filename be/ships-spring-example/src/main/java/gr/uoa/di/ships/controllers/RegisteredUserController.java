package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registered-user")
public class RegisteredUserController {

  private final RegisteredUserService registeredUserService;

  public RegisteredUserController(RegisteredUserService registeredUserService) {
    this.registeredUserService = registeredUserService;
  }

  @GetMapping("/get-user-info/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserInfoDTO getUserInfo(@PathVariable Long id) {
    return registeredUserService.getUserInfo(id);
  }

  @PutMapping("/change-password/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void changePassword(@PathVariable Long id, @RequestBody String newPassword) {
    registeredUserService.changePassword(id, newPassword);
  }
}