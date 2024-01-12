package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.service.UserService;
import java.util.List;
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
  private final UserMapper userMapper = UserMapper.INSTANCE;

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto requestDto) {
    return new ResponseEntity<>(userService.save(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> changeUserRole(@RequestBody ChangeUserRoleDto requestDto) {
    return new ResponseEntity<>(userService.updateUserRole(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<List<UserDto>> getAll() {
    return new ResponseEntity<>(
        userService.getAll().stream().map(userMapper::userToUserDto).toList(), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(userMapper.userToUserDto(userService.getById(id)), HttpStatus.OK);
  }
}
