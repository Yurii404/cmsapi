package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper = UserMapper.INSTANCE;

  public UserDto save(UserRegisterDto registerDto) {
    User userToSave = userMapper.userRegisterDtoToUser(registerDto);

    return userMapper.userToUserDto(userRepository.save(userToSave));
  }

  public UserDto updateUserRole(ChangeUserRoleDto requestDto) {
    User userFromRep =
        userRepository
            .findById(requestDto.getId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("User with id: %s does not exist!", requestDto.getId())));

    userFromRep.setRole(requestDto.getNewRole());

    return userMapper.userToUserDto(userRepository.save(userFromRep));
  }

  public List<UserDto> getAll() {
    return userRepository.findAll().stream()
        .map(userMapper::userToUserDto)
        .collect(Collectors.toList());
  }

  public User getInstructorById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.INSTRUCTOR)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Instructor with id: %s does not exist!", userId)));
  }
}
