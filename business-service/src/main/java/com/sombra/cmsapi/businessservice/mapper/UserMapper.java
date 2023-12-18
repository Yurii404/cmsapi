package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.UserDto;
import com.sombra.cmsapi.businessservice.dto.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  User userRegisterDtoToUser(UserRegisterDto user);

  UserDto userToUserDto(User user);
}
