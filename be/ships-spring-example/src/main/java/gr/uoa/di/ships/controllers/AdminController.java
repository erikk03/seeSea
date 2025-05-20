package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final VesselService vesselService;

  public AdminController(VesselService vesselService) {
    this.vesselService = vesselService;
  }

  @PutMapping("/change-vessel-type")
  @ResponseStatus(HttpStatus.OK)
  public void updateVesselType(@RequestBody UpdateVesselDTO updateVesselDTO) {
    vesselService.updateVesselType(updateVesselDTO);
  }
}
