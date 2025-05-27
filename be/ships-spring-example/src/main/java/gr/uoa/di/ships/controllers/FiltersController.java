package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.api.dto.CurrentFiltersDTO;
import gr.uoa.di.ships.services.interfaces.FiltersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filters")
public class FiltersController {

  private final FiltersService filtersService;

  FiltersController(FiltersService filtersService) {
    this.filtersService = filtersService;
  }

  @GetMapping("/get-available-filters")
  AvailableFiltersDTO getAvailableFilters() {
    return filtersService.getAvailableFilters();
  }

  @GetMapping("/get-current-filters")
    CurrentFiltersDTO getCurrentFilters() {
    return filtersService.getCurrentFilters();
  }
}
