package gr.uoa.di.ships.configurations.migrations;

import gr.uoa.di.ships.persistence.model.enums.MigrationEnum;
import gr.uoa.di.ships.services.interfaces.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CheckMigrations {

  private final MigrationService migrationService;

  public CheckMigrations(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  // will run anything that is inside the method before the application starts
  @Bean
  public CommandLineRunner initDatabase() {
    boolean migrationNeeded = false;
    if (!migrationService.completedMigration(MigrationEnum.LOAD_VESSEL_TYPES_CSV)) {
      migrationService.loadVesselTypesFromCSV();
      migrationNeeded = true;
    }
    if (!migrationService.completedMigration(MigrationEnum.LOAD_VESSEL_STATUS_CSV)) {
      migrationService.loadVesselStatusFromCSV();
      migrationNeeded = true;
    }
    if (migrationNeeded) {
      return args -> log.info("Migrations completed successfully");
    }
    return args -> log.info("Migrations are already completed");
  }
}