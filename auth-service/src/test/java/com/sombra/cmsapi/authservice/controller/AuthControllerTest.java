package com.sombra.cmsapi.authservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.authservice.dto.AuthRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthResponseDto;
import com.sombra.cmsapi.authservice.dto.UserRegisterDto;
import com.sombra.cmsapi.authservice.enumerated.UserRole;
import com.sombra.cmsapi.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  @Test
  void Should_Register_When_ValidRequest() {
    // SETUP
    UserRegisterDto userRegisterDto =
        UserRegisterDto.builder()
            .firstName("Name")
            .lastName("Description")
            .role(UserRole.STUDENT)
            .email("test@mail.com")
            .password("password")
            .build();

    AuthResponseDto authResponseDto =
        AuthResponseDto.builder().accessToken("test").refreshToken("test").build();

    when(authService.register(userRegisterDto)).thenReturn(authResponseDto);

    // ACT
    ResponseEntity<AuthResponseDto> responseEntity = authController.register(userRegisterDto);

    // VERIFY
    verify(authService, times(1)).register(userRegisterDto);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(authResponseDto, responseEntity.getBody());
  }

  @Test
  void Should_Authenticate_When_ValidRequest() {
    // SETUP
    AuthRequestDto authRequestDto =
        AuthRequestDto.builder().email("test@mail.com").password("password").build();

    AuthResponseDto authResponseDto =
        AuthResponseDto.builder().accessToken("test").refreshToken("test").build();

    when(authService.authenticate(authRequestDto)).thenReturn(authResponseDto);

    // ACT
    ResponseEntity<AuthResponseDto> responseEntity = authController.authenticate(authRequestDto);

    // VERIFY
    verify(authService, times(1)).authenticate(authRequestDto);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(authResponseDto, responseEntity.getBody());
  }

  @Test
  void Should_ValidateToken_When_ValidRequest() {
    // SETUP
    String token = "token";

    when(authService.validateToken(token)).thenReturn(true);

    // ACT
    ResponseEntity<Boolean> responseEntity = authController.validateToken(token);

    // VERIFY
    verify(authService, times(1)).validateToken(token);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(true, responseEntity.getBody());
  }
}
