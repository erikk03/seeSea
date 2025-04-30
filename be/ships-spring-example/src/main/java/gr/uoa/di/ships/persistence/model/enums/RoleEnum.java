package gr.uoa.di.ships.persistence.model.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum RoleEnum {
  REGISTERED_USER("Registered User"),
  ADMINISTRATOR("Administrator"),
  ANONYMOUS_USER("Anonymous User");

  final String description;

  RoleEnum(final String description) {
      this.description = description;
  }

  public static RoleEnum fromRole(String role) {
    return Arrays.stream(RoleEnum.values())
        .filter(s -> s.name().equals(role))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such role: " + role));
  }

  public static RoleEnum fromDescription(String description) {
    return Arrays.stream(RoleEnum.values())
        .filter(s -> s.description.equals(description))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such description for role: " + description));
  }
}
