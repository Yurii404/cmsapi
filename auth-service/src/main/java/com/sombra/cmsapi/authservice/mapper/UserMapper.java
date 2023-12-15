package com.sombra.cmsapi.authservice.mapper;

import com.sombra.cmsapi.authservice.dto.UserDetailsResponseDto;
import com.sombra.cmsapi.authservice.dto.UserRegisterDto;
import com.sombra.cmsapi.authservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDetailsResponseDto userToUserDetailsResponseDto(User user);


    User userRegisterDtoToUser(UserRegisterDto user);
}
