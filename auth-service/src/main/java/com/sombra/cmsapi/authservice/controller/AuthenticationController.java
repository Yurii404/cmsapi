package com.sombra.cmsapi.authservice.controller;

import com.sombra.cmsapi.authservice.dto.AuthenticationRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthenticationResponseDto;
import com.sombra.cmsapi.authservice.dto.RegisterRequestDto;
import com.sombra.cmsapi.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(
            @RequestBody RegisterRequestDto request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}