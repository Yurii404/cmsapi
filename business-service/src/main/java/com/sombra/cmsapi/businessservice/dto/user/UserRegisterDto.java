package com.sombra.cmsapi.businessservice.dto.user;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserRegisterDto {

  @NotEmpty(message = "First name cannot be null or empty")
  private String firstName;

  @NotEmpty(message = "Last name cannot be null or empty")
  private String lastName;

  @NotEmpty(message = "Email cannot be null or empty")
  @Email
  private String email;

  @NotEmpty(message = "Password cannot be null or empty")
  private String password;

  @NotEmpty(message = "Role cannot be null or empty")
  private UserRole role;
}
