package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.ChangePasswordDTO;
import gr.uoa.di.ships.api.dto.ChangeUsernameDTO;
import gr.uoa.di.ships.api.dto.JwtTokenDTO;
import gr.uoa.di.ships.api.dto.MyFleetDTO;
import gr.uoa.di.ships.api.dto.UserInfoDTO;
import gr.uoa.di.ships.services.interfaces.MyFleetService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registered-user")
public class RegisteredUserController {

  private final RegisteredUserService registeredUserService;
  private final MyFleetService myFleetService;

  public RegisteredUserController(RegisteredUserService registeredUserService,
                                  MyFleetService myFleetService) {
    this.registeredUserService = registeredUserService;
    this.myFleetService = myFleetService;
  }

  @GetMapping("/get-user-info")
  @ResponseStatus(HttpStatus.OK)
  public UserInfoDTO getUserInfo() {
    return registeredUserService.getUserInfo();
  }

  @PutMapping("/change-password")
  @ResponseStatus(HttpStatus.OK)
  public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
    registeredUserService.changePassword(changePasswordDTO);
  }

  @PutMapping("/change-username")
  @ResponseStatus(HttpStatus.OK)
  public JwtTokenDTO changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
    return registeredUserService.changeUsername(changeUsernameDTO);
  }

  @GetMapping("/get-my-fleet")
  @ResponseStatus(HttpStatus.OK)
  public MyFleetDTO getMyFleet() {
    return myFleetService.getMyFleet();
  }

  @PutMapping("/add-vessel-to-fleet")
  @ResponseStatus(HttpStatus.OK)
  public void addVesselToFleet(@RequestBody String mmsi) {
    myFleetService.addVesselToFleet(mmsi);
  }

  @PutMapping("/remove-vessel-from-fleet")
  @ResponseStatus(HttpStatus.OK)
  public void removeVesselFromFleet(@RequestBody String mmsi) {
    myFleetService.removeVesselFromFleet(mmsi);
  }

  @DeleteMapping("/delete-user")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@RequestParam String password) {
    registeredUserService.deleteRegisteredUser(password);
  }
}