package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @Test
  void Should_RegisterUser_When_ValidRequest() {
    // SETUP
    UserRegisterDto userRegisterDto =
        UserRegisterDto.builder()
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    UserDto expectedUserDto =
        UserDto.builder()
            .id("1")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    when(userService.save(userRegisterDto)).thenReturn(expectedUserDto);

    // ACT
    ResponseEntity<UserDto> responseEntity = userController.register(userRegisterDto);

    // VERIFY
    verify(userService, times(1)).save(userRegisterDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedUserDto, responseEntity.getBody());
  }

  @Test
  void Should_ChangeUserRole_When_ValidRequest() {
    // SETUP
    ChangeUserRoleDto changeUserRoleDto =
        ChangeUserRoleDto.builder().id("1").newRole(UserRole.INSTRUCTOR).build();

    UserDto expectedUserDto =
        UserDto.builder()
            .id("1")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.INSTRUCTOR)
            .password("password")
            .build();

    when(userService.updateUserRole(changeUserRoleDto)).thenReturn(expectedUserDto);

    // ACT
    ResponseEntity<UserDto> responseEntity = userController.changeUserRole(changeUserRoleDto);

    // VERIFY
    verify(userService, times(1)).updateUserRole(changeUserRoleDto);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedUserDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    UserDto expectedUserDto =
        UserDto.builder()
            .id("1")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.INSTRUCTOR)
            .password("password")
            .build();

    when(userService.getById("1")).thenReturn(expectedUserDto);

    // ACT
    ResponseEntity<UserDto> responseEntity = userController.getById("1");

    // VERIFY
    verify(userService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedUserDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
    // SETUP
    UserDto expectedUserDtoFirst =
        UserDto.builder()
            .id("1")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.INSTRUCTOR)
            .password("password")
            .build();

    UserDto expectedUserDtoSecond =
        UserDto.builder()
            .id("2")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    Page<UserDto> pageUser = new PageImpl<>(List.of(expectedUserDtoFirst, expectedUserDtoSecond));

    when(userService.getAll(any())).thenReturn(pageUser);

    // ACT
    ResponseEntity<Page<UserDto>> responseEntity = userController.getAll(Pageable.unpaged());

    // VERIFY
    verify(userService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageUser, responseEntity.getBody());
  }
}
