package com.sombra.cmsapi.authservice.controller;


import com.sombra.cmsapi.authservice.dto.UserDetailsResponseDto;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-details")
@AllArgsConstructor
public class UserDetailsController {

    private final UserRepository userRepository;

    @GetMapping("{userMail}")
    public ResponseEntity<UserDetailsResponseDto> getByEmail(@PathVariable String userMail) {
        User user = userRepository.findByEmail(userMail).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        return new ResponseEntity<>(UserDetailsResponseDto.builder()
                .firstName(user.getFirstname())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build(),
                HttpStatus.OK);
    }
}
