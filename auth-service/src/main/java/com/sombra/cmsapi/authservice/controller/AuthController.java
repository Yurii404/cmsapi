package com.sombra.cmsapi.authservice.controller;

import com.sombra.cmsapi.authservice.dto.AuthRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthResponseDto;
import com.sombra.cmsapi.authservice.dto.UserRegisterDto;
import com.sombra.cmsapi.authservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegisterDto request) {
    return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request) {
    return new ResponseEntity<>(authService.authenticate(request), HttpStatus.OK);
  }

  @GetMapping("/validate/{token}")
  public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
    return new ResponseEntity<>(authService.validateToken(token), HttpStatus.OK);
  }
}
