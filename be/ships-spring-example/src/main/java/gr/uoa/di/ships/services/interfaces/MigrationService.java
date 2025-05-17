package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.persistence.model.enums.MigrationEnum;

public interface MigrationService {

  void loadVesselTypesFromCSV();

  void loadVesselStatusFromCSV();

  boolean completedMigration(MigrationEnum migrationEnum);
}
