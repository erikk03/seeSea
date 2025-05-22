package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/get-user-info")
  @ResponseStatus(HttpStatus.OK)
  public UserInfoDTO getUserInfo() {
    return registeredUserService.getUserInfo();
  }

  @PutMapping("/change-password")
  @ResponseStatus(HttpStatus.OK)
  public void changePassword(@RequestBody String newPassword) {
    registeredUserService.changePassword(newPassword);
  }

  @PutMapping("/add-vessel-to-fleet")
  @ResponseStatus(HttpStatus.OK)
  public void addVesselToFleet(@RequestBody String mmsi) {
    registeredUserService.addVesselToFleet(mmsi);
  }

  @PutMapping("/remove-vessel-from-fleet")
  @ResponseStatus(HttpStatus.OK)
  public void removeVesselFromFleet(@RequestBody String mmsi) {
    registeredUserService.removeVesselFromFleet(mmsi);
  }
}