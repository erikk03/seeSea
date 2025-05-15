package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.services.interfaces.MigrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migration")
class MigrationsController {

    private final MigrationService migrationService;

  MigrationsController(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @PostMapping("/load-vessel-types-csv")
    public void loadVesselTypesFromCSV() {
      migrationService.loadVesselTypesFromCSV();
    }
}