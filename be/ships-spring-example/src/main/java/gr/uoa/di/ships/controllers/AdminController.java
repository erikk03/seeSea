package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.SelectOptionDTO;
import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final VesselService vesselService;
  private final VesselTypeService vesselTypeService;

  public AdminController(VesselService vesselService, VesselTypeService vesselTypeService) {
    this.vesselService = vesselService;
    this.vesselTypeService = vesselTypeService;
  }

  @PutMapping("/change-vessel-type")
  @ResponseStatus(HttpStatus.OK)
  public void updateVesselType(@RequestBody UpdateVesselDTO updateVesselDTO) {
    vesselService.updateVesselType(updateVesselDTO);
  }

  @GetMapping("/get-vessel-types")
  @ResponseStatus(HttpStatus.OK)
  public List<SelectOptionDTO> getAllVesselTypes() {
    return vesselTypeService.getAllVesselTypes();
  }
}
