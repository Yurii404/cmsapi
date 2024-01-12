package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.UserMapper;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
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

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User getById(String userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("User with id: %s does not exist!", userId)));
  }
}
