package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.UserDto;
import com.sombra.cmsapi.businessservice.dto.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserDto save(UserRegisterDto registerDto){
        User userToSave = User.builder()
                .firstname(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .role(registerDto.getRole())
                .build();

        User saveduser = userRepository.save(userToSave);

        return UserDto.builder()
                .id(saveduser.getId())
                .firstName(saveduser.getFirstname())
                .lastName(saveduser.getLastName())
                .email(saveduser.getEmail())
                .password(saveduser.getPassword())
                .role(saveduser.getRole())
                .build();
    }
}
