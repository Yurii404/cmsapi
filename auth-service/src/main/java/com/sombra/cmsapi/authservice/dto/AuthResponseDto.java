package com.sombra.cmsapi.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponseDto {
  private String accessToken;
  private String refreshToken;
}
