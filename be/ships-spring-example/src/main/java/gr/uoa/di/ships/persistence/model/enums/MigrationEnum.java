package gr.uoa.di.ships.persistence.model.enums;

import lombok.Getter;

@Getter
public enum MigrationEnum {
  LOAD_VESSEL_TYPES_CSV("Load vessel types from CSV"),
  LOAD_VESSEL_STATUS_CSV("Load vessel status from CSV");

  final String description;

  MigrationEnum(final String description) {
    this.description = description;
  }
}
