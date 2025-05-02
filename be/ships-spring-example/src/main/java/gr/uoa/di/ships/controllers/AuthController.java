package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.UserAuthDTO;
import gr.uoa.di.ships.api.dto.UserRegisterDTO;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final RegisteredUserService registeredUserService;

  public AuthController(RegisteredUserService registeredUserService) {
    this.registeredUserService = registeredUserService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody UserRegisterDTO userDTO) {
    registeredUserService.register(userDTO);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public JwtTokenDTO login(@RequestBody UserAuthDTO userAuthDTO) {
    return registeredUserService.verify(userAuthDTO);
  }
}
