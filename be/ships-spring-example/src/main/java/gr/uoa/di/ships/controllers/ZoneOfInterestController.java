package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.GetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.services.interfaces.ZoneOfInterestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zone-of-interest")
public class ZoneOfInterestController {

  private final ZoneOfInterestService zoneOfInterestService;

  public ZoneOfInterestController(ZoneOfInterestService zoneOfInterestService) {
    this.zoneOfInterestService = zoneOfInterestService;
  }

  @GetMapping("/get-zone")
  @ResponseStatus(HttpStatus.OK)
  public GetZoneOfInterestDTO getZoneOfInterest() {
    return zoneOfInterestService.getZoneOfInterest();
  }

  @PostMapping("/set-zone")
  @ResponseStatus(HttpStatus.OK)
  public void setZoneOfInterest(@RequestBody SetZoneOfInterestDTO setZoneOfInterestDTO) {
    zoneOfInterestService.setZoneOfInterest(setZoneOfInterestDTO);
  }

  @DeleteMapping("/remove-zone")
  @ResponseStatus(HttpStatus.OK)
  public void removeZoneOfInterest(@RequestParam Long id) {
    zoneOfInterestService.deleteZoneOfInterest(id);
  }

  @GetMapping("/get-zone-options")
  @ResponseStatus(HttpStatus.OK)
  public GetZoneOfInterestOptionsDTO getZoneOfInterestOptions() {
    return zoneOfInterestService.getZoneOfInterestOptions();
  }

  @PostMapping("/set-zone-options")
  @ResponseStatus(HttpStatus.OK)
  public void setZoneOfInterestOptions(@RequestBody SetZoneOfInterestOptionsDTO setZoneOfInterestOptionsDTO) {
    zoneOfInterestService.setZoneOfInterestOptions(setZoneOfInterestOptionsDTO);
  }
}
