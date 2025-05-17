package gr.uoa.di.ships.persistence.model.enums;

import lombok.Getter;

@Getter
public enum FIlterFromEnum {
  MY_FLEET("MyFleet"),
  ALL("All");

  final String description;

  FIlterFromEnum(final String description) {
      this.description = description;
  }
}
