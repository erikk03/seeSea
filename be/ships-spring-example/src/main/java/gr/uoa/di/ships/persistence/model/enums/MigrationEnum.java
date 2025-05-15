package gr.uoa.di.ships.persistence.model.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum MigrationEnum {
  LOAD_VESSEL_TYPES_CSV("Load vessel types from CSV");

  final String description;

  MigrationEnum(final String description) {
      this.description = description;
  }

  public static MigrationEnum fromMigration(String migration) {
    return Arrays.stream(MigrationEnum.values())
        .filter(s -> s.name().equals(migration))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such migration: " + migration));
  }

  public static MigrationEnum fromDescription(String description) {
    return Arrays.stream(MigrationEnum.values())
        .filter(m -> m.description.equals(description))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such description for migration: " + description));
  }
}
