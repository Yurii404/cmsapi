package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
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

  public Page<UserDto> getAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(userMapper::userToUserDto);
  }

  public UserDto getById(String userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::userToUserDto)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("User with id: %s does not exist!", userId)));
  }
}
