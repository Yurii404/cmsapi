package com.sombra.cmsapi.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthRequestDto {
    private String email;
    private String password;
}
