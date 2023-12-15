package com.sombra.cmsapi.authservice.controller;


import com.sombra.cmsapi.authservice.dto.UserDetailsResponseDto;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.mapper.UserMapper;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-details")
@AllArgsConstructor
public class UserDetailsController {

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @GetMapping("{userMail}")
    public ResponseEntity<UserDetailsResponseDto> getByEmail(@PathVariable String userMail) {
        User user = userRepository.findByEmail(userMail).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        return new ResponseEntity<>(userMapper.userToUserDetailsResponseDto(user), HttpStatus.OK);
    }
}
