package com.sombra.cmsapi.authservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.authservice.dto.UserDetailsResponseDto;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.enumerated.UserRole;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserDetailsControllerTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsController userDetailsController;

  @Test
  void Should_GetByEmail_When_ValidRequest() {
    // SETUP
    String email = "test@mail.com";
    User user =
        User.builder()
            .id("1")
            .firstName("Name")
            .lastName("Description")
            .role(UserRole.STUDENT)
            .email("test@mail.com")
            .password("password")
            .build();

    UserDetailsResponseDto userDetailsResponseDto =
        UserDetailsResponseDto.builder()
            .firstName("Name")
            .lastName("Description")
            .role(UserRole.STUDENT)
            .email("test@mail.com")
            .password("password")
            .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    // ACT
    ResponseEntity<UserDetailsResponseDto> responseEntity = userDetailsController.getByEmail(email);

    // VERIFY
    verify(userRepository, times(1)).findByEmail(email);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(userDetailsResponseDto, responseEntity.getBody());
  }
}
