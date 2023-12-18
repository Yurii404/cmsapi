package com.sombra.cmsapi.businessservice.dto;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterDto {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private UserRole role;
}
