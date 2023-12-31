package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.ChangeuserRoleDto;
import com.sombra.cmsapi.businessservice.dto.UserDto;
import com.sombra.cmsapi.businessservice.dto.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.exception.UserNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper = UserMapper.INSTANCE;

  public UserDto save(UserRegisterDto registerDto) {
    User userToSave = userMapper.userRegisterDtoToUser(registerDto);

    User saveduser = userRepository.save(userToSave);

    return userMapper.userToUserDto(saveduser);
  }

  public UserDto updateUserRole(ChangeuserRoleDto requestDto) {
    User userFromRep =
        userRepository
            .findById(requestDto.getId())
            .orElseThrow(() -> new UserNotFoundException(requestDto.getId()));

    userFromRep.setRole(requestDto.getNewRole());
    User updatedUser = userRepository.save(userFromRep);

    return userMapper.userToUserDto(updatedUser);
  }
}
