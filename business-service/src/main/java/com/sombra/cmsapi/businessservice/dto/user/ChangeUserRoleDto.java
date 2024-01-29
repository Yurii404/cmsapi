package com.sombra.cmsapi.businessservice.dto.user;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChangeUserRoleDto {
  @NotEmpty(message = "Id cannot be null or empty")
  private String id;

  @NotEmpty(message = "New role cannot be null or empty")
  private UserRole newRole;
}
