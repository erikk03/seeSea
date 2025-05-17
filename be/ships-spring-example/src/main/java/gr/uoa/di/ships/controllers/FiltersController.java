package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.AvailableFiltersDTO;
import gr.uoa.di.ships.services.interfaces.FilterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filters")
class FiltersController {

  private final FilterService filterService;

  FiltersController(FilterService filterService) {
    this.filterService = filterService;
  }

  @GetMapping("/get-available-filters")
  AvailableFiltersDTO getAvailableFilters() {
    return filterService.getAvailableFilters();
  }
}