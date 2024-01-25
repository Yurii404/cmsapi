package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Spy private UserMapper userMapper = UserMapper.INSTANCE;

  @InjectMocks private UserService userService;

  @Test
  void Should_Save_When_ValidData() {
    UserRegisterDto registerDto =
        UserRegisterDto.builder()
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();
    User savedUser = userMapper.userRegisterDtoToUser(registerDto);
    savedUser.setId("1");

    ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

    userService.save(registerDto);

    verify(userRepository, times(1)).save(any());
    verify(userRepository).save(argumentCaptor.capture());
    assertEquals(registerDto.getEmail(), argumentCaptor.getValue().getEmail());
    assertEquals(registerDto.getPassword(), argumentCaptor.getValue().getPassword());
    assertEquals(registerDto.getRole(), argumentCaptor.getValue().getRole());
    assertEquals(registerDto.getFirstName(), argumentCaptor.getValue().getFirstName());
    assertEquals(registerDto.getLastName(), argumentCaptor.getValue().getLastName());
  }

  @Test
  void Should_UpdateUserRole_When_ValidData() {
    ChangeUserRoleDto requestDto = new ChangeUserRoleDto("1111", UserRole.INSTRUCTOR);
    User userFromRepository =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    when(userRepository.findById(any())).thenReturn(Optional.of(userFromRepository));
    ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

    userService.updateUserRole(requestDto);

    verify(userRepository, times(1)).save(any());
    verify(userRepository).save(argumentCaptor.capture());
    assertEquals(requestDto.getNewRole(), argumentCaptor.getValue().getRole());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnUpdateUserRole_When_UserDoesNotExist() {
    ChangeUserRoleDto requestDto = new ChangeUserRoleDto("1111", UserRole.INSTRUCTOR);

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              userService.updateUserRole(requestDto);
            });

    Assertions.assertEquals("User with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetAll_When_Exist() {
    User firstUser =
        User.builder()
            .id("1111")
            .email("testFirst@mail")
            .password("password1")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    User secondUser =
        User.builder()
            .id("2222")
            .email("testSecond@mail")
            .password("password2")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    User thirdUser =
        User.builder()
            .id("3333")
            .email("testThird@mail")
            .password("password3")
            .role(UserRole.ADMIN)
            .firstName("John")
            .lastName("Doe")
            .build();

    List<User> userList = List.of(firstUser, secondUser, thirdUser);
    Page<User> userPage = new PageImpl<>(userList);

    when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

    Page<UserDto> result = userService.getAll(Pageable.unpaged());

    assertNotNull(result);
    assertEquals(result.getTotalElements(), userList.size());
    verify(userRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_GetById_When_Exists() {
    String userId = "1";
    User userFromRepository =
        User.builder()
            .id("1")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    when(userRepository.findById(any())).thenReturn(Optional.of(userFromRepository));

    UserDto result = userService.getById(userId);

    assertNotNull(result);
    assertEquals(userFromRepository.getId(), result.getId());
    verify(userRepository, times(1)).findById(any());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {
    String userId = "nonExistentId";

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              userService.getById(userId);
            });

    Assertions.assertEquals("User with id: nonExistentId does not exist!", thrown.getMessage());
  }
}
