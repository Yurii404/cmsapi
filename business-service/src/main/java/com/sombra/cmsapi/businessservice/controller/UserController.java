package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.ChangeuserRoleDto;
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
  public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto requestDto) {
    return new ResponseEntity<>(userService.save(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> changeUserRole(@RequestBody ChangeuserRoleDto requestDto) {
    return new ResponseEntity<>(userService.updateUserRole(requestDto), HttpStatus.OK);
  }
}
