package gr.uoa.di.ships.persistence.model.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum RoleEnum {
  REGISTERED_USER("Registered User"),
  ADMINISTRATOR("Administrator"),
  ANONYMOUS_USER("Anonymous User");

  final String authority;

  RoleEnum(final String authority) {
    this.authority = authority;
  }

  public static RoleEnum fromRole(String role) {
    return Arrays.stream(RoleEnum.values())
        .filter(s -> s.name().equals(role))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such role: " + role));
  }

  public static RoleEnum fromAuthority(String description) {
    return Arrays.stream(RoleEnum.values())
        .filter(s -> s.authority.equals(description))
        .findAny().orElseThrow(() -> new IllegalArgumentException("No such description for role: " + description));
  }
}
