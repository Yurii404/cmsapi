package com.sombra.cmsapi.businessservice.dto;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChangeuserRoleDto {
  private String id;
  private UserRole newRole;
}
