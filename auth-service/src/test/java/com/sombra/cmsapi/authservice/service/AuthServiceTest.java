package com.sombra.cmsapi.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.authservice.dto.AuthRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthResponseDto;
import com.sombra.cmsapi.authservice.dto.UserRegisterDto;
import com.sombra.cmsapi.authservice.entity.Token;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.enumerated.TokenType;
import com.sombra.cmsapi.authservice.enumerated.UserRole;
import com.sombra.cmsapi.authservice.exception.EmailAlreadyExistsException;
import com.sombra.cmsapi.authservice.exception.UserNotRegisteredException;
import com.sombra.cmsapi.authservice.repository.TokenRepository;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private RestTemplate restTemplate;
  @Mock private JwtUtil jwtUtil;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserRepository userRepository;
  @Mock private TokenRepository tokenRepository;
  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks private AuthService authService;

  @Test
  void Should_Register_When_ValidData() {
    UserRegisterDto requestDto =
        UserRegisterDto.builder()
            .email("test@example.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .build();

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("hashedPassword");
    doReturn(new ResponseEntity<>(HttpStatus.OK))
        .when(restTemplate)
        .postForEntity(anyString(), any(), eq(Object.class));
    when(jwtUtil.generateToken(any(), eq("ACCESS"))).thenReturn("accessToken");
    when(jwtUtil.generateToken(any(), eq("REFRESH"))).thenReturn("refreshToken");
    ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

    AuthResponseDto response = authService.register(requestDto);

    assertNotNull(response);
    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
    verify(userRepository).findByEmail(requestDto.getEmail());
    verify(restTemplate)
        .postForEntity(
            eq("http://business-service/users/register"), eq(requestDto), eq(Object.class));
    verify(jwtUtil).generateToken(any(), eq("ACCESS"));
    verify(jwtUtil).generateToken(any(), eq("REFRESH"));
    verify(userRepository).save(argumentCaptor.capture());
    assertEquals(requestDto.getEmail(), argumentCaptor.getValue().getEmail());
    assertEquals(requestDto.getFirstName(), argumentCaptor.getValue().getFirstName());
    assertEquals(requestDto.getLastName(), argumentCaptor.getValue().getLastName());
    assertEquals(requestDto.getRole(), argumentCaptor.getValue().getRole());
  }

  @Test
  void Should_ThrowEmailAlreadyExistsExceptionOnRegister_When_AlreadyRegistered() {
    UserRegisterDto requestDto =
        UserRegisterDto.builder().role(UserRole.ADMIN).email("test@example.com").build();

    when(userRepository.findByEmail(requestDto.getEmail()))
        .thenReturn(Optional.of(User.builder().build()));

    EmailAlreadyExistsException thrown =
        Assertions.assertThrows(
            EmailAlreadyExistsException.class,
            () -> {
              authService.register(requestDto);
            });

    Assertions.assertEquals(
        "User with email 'test@example.com' already exists", thrown.getMessage());
  }

  @Test
  void Should_ThrowUserNotRegisteredExceptionOnRegister_When_BusinessServiceFailed() {
    UserRegisterDto requestDto =
        UserRegisterDto.builder()
            .email("test@example.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .build();

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("hashedPassword");
    doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Something went wrong"))
        .when(restTemplate)
        .postForEntity(anyString(), any(), eq(Object.class));

    UserNotRegisteredException thrown =
        Assertions.assertThrows(
            UserNotRegisteredException.class,
            () -> {
              authService.register(requestDto);
            });

    Assertions.assertEquals(
        "User was not registered: 400 Something went wrong", thrown.getMessage());
  }

  @Test
  void Should_Authenticate_When_ValidData() {
    AuthRequestDto request =
        AuthRequestDto.builder().email("test@example.com").password("password").build();
    User userDetails =
        User.builder()
            .id("1111")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.ADMIN)
            .email("test@example.com")
            .password("hashedPassword")
            .tokens(new ArrayList<>())
            .build();

    when(authenticationManager.authenticate(any()))
        .thenReturn(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userDetails));
    when(jwtUtil.generateToken(userDetails, "ACCESS")).thenReturn("accessToken");
    when(jwtUtil.generateToken(userDetails, "REFRESH")).thenReturn("refreshToken");

    AuthResponseDto response = authService.authenticate(request);

    assertNotNull(response);
    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
    verify(authenticationManager)
        .authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    verify(userRepository).findByEmail(request.getEmail());
    verify(jwtUtil).generateToken(userDetails, "ACCESS");
    verify(jwtUtil).generateToken(userDetails, "REFRESH");
  }

  @Test
  void Should_ReturnTrueOnValidateToken_When_ExpiredOrRevoked() {
    String token = "validToken";
    Token tokenFromRepo = Token.builder().accessToken(token).expired(false).revoked(false).build();

    when(tokenRepository.findByAccessToken(any())).thenReturn(Optional.of(tokenFromRepo));

    Boolean result = authService.validateToken(token);

    verify(tokenRepository, times(1)).findByAccessToken(any());
    assertTrue(result);
  }

  @Test
  void Should_ReturnFalseOnValidateToken_When_ExpiredOrRevoked() {
    String token = "validToken";
    Token tokenFromRepo = Token.builder().accessToken(token).expired(true).revoked(true).build();

    when(tokenRepository.findByAccessToken(any())).thenReturn(Optional.of(tokenFromRepo));

    Boolean result = authService.validateToken(token);

    verify(tokenRepository, times(1)).findByAccessToken(any());
    assertFalse(result);
  }

  @Test
  void Should_Save_When_ValidData() {
    String accessToken = "validAccessToken";
    String refreshToken = "validRefreshToken";
    User user = User.builder().id("1111").build();

    ArgumentCaptor<Token> argumentCaptor = ArgumentCaptor.forClass(Token.class);

    ReflectionTestUtils.invokeMethod(authService, "saveUserToken", user, accessToken, refreshToken);

    verify(tokenRepository).save(argumentCaptor.capture());
    assertEquals(user, argumentCaptor.getValue().getUser());
    assertEquals(accessToken, argumentCaptor.getValue().getAccessToken());
    assertEquals(refreshToken, argumentCaptor.getValue().getRefreshToken());
    assertEquals(TokenType.BEARER, argumentCaptor.getValue().getTokenType());
    assertFalse(argumentCaptor.getValue().isExpired());
    assertFalse(argumentCaptor.getValue().isRevoked());
  }

  @Test
  void Should_RevokeAllUserTokens_When_ValidData() {
    User user = User.builder().id("1111").build();

    List<Token> tokens =
        List.of(
            Token.builder().revoked(false).expired(false).build(),
            Token.builder().revoked(false).expired(false).build());

    when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(tokens);
    ArgumentCaptor<List<Token>> argumentCaptor = ArgumentCaptor.forClass(List.class);

    ReflectionTestUtils.invokeMethod(authService, "revokeAllUserTokens", user);

    verify(tokenRepository).saveAll(argumentCaptor.capture());
    verify(tokenRepository).saveAll(argumentCaptor.capture());
    assertEquals(tokens.size(), argumentCaptor.getValue().size());
    assertTrue(argumentCaptor.getValue().get(0).isRevoked());
    assertTrue(argumentCaptor.getValue().get(0).isExpired());
    assertTrue(argumentCaptor.getValue().get(1).isRevoked());
    assertTrue(argumentCaptor.getValue().get(1).isExpired());
  }

  @Test
  void Should_NotThrowOnValidateUserRole_When_ValidRole() {
    UserRegisterDto registerDto = UserRegisterDto.builder().role(UserRole.STUDENT).build();

    ReflectionTestUtils.invokeMethod(authService, "validateUserRole", registerDto);
  }
}
