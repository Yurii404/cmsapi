package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.UserDto;
import com.sombra.cmsapi.businessservice.dto.UserRegisterDto;
import com.sombra.cmsapi.businessservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto registerDto) {
    return new ResponseEntity<>(userService.save(registerDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('STUDENT')")
  @GetMapping("/secured")
  public ResponseEntity<String> secured() {
    return new ResponseEntity<>("has role student", HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/securedAD")
  public ResponseEntity<String> securedAd() {
    return new ResponseEntity<>("has role admin", HttpStatus.OK);
  }
}
