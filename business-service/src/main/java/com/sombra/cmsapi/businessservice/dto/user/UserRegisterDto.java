package com.sombra.cmsapi.businessservice.dto.user;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserRegisterDto {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private UserRole role;
}
