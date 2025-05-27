package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.FiltersDTO;
import gr.uoa.di.ships.api.dto.VesselHistoryDataDTO;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vessel")
public class VesselController {
  
  private final VesselHistoryDataService vesselHistoryDataService;

  VesselController(VesselHistoryDataService vesselHistoryDataService) {
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @PostMapping("/set-filters-and-get-map")
  List<VesselHistoryDataDTO> setFiltersAndGetMap(@RequestBody FiltersDTO filtersDTO) {
    return vesselHistoryDataService.setFiltersAndGetMap(filtersDTO);
  }

  @GetMapping("/get-map")
  List<VesselHistoryDataDTO> getMap() {
    return vesselHistoryDataService.getMap();
  }

  @GetMapping("get-vessel-history")
  public List<VesselHistoryDataDTO> getVesselHistory(@RequestParam String mmsi) {
    return vesselHistoryDataService.getVesselHistoryForTwelveHours(mmsi);
  }
}