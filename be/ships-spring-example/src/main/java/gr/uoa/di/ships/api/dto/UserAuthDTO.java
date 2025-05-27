package gr.uoa.di.ships.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthDTO {
  private String email;
  private String username;
  @NotNull
  private String password;

  @AssertTrue(message = "Either email or username must not be null")
  public boolean isEmailOrUsernameValid() {
    return email != null || username != null;
  }
}
