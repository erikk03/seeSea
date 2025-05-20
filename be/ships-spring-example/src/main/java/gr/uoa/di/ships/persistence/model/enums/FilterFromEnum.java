package gr.uoa.di.ships.persistence.model.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum FilterFromEnum {
  MY_FLEET("MyFleet"),
  ALL("All");

  final String description;

  FilterFromEnum(final String description) {
    this.description = description;
  }

  public static boolean isValidFilterFrom(String filterFrom) {
    return Arrays.stream(values())
        .anyMatch(e -> e.getDescription().equals(filterFrom));
  }
}
