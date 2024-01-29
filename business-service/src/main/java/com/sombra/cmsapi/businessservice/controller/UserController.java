package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterDto requestDto) {
    return new ResponseEntity<>(userService.save(requestDto), HttpStatus.CREATED);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> changeUserRole(@Valid @RequestBody ChangeUserRoleDto requestDto) {
    return new ResponseEntity<>(userService.updateUserRole(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<Page<UserDto>> getAll(Pageable pageable) {
    return new ResponseEntity<>(userService.getAll(pageable), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
  }
}
