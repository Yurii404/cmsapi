package com.sombra.cmsapi.authservice.service;

import com.sombra.cmsapi.authservice.dto.AuthRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthResponseDto;
import com.sombra.cmsapi.authservice.dto.UserRegisterDto;
import com.sombra.cmsapi.authservice.entity.Token;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.enumerated.TokenType;
import com.sombra.cmsapi.authservice.enumerated.UserRole;
import com.sombra.cmsapi.authservice.exception.EmailAlreadyExistsException;
import com.sombra.cmsapi.authservice.exception.RoleNotFoundException;
import com.sombra.cmsapi.authservice.exception.TokenNotFoundException;
import com.sombra.cmsapi.authservice.exception.UserNotRegisteredException;
import com.sombra.cmsapi.authservice.mapper.UserMapper;
import com.sombra.cmsapi.authservice.repository.TokenRepository;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

  private final RestTemplate restTemplate;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper = UserMapper.INSTANCE;
  private final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

  public AuthResponseDto register(UserRegisterDto requestDto) {
    // Do check if the role is valid
    validateUserRole(requestDto);

    // Do check if the user with the email already exist
    userRepository
        .findByEmail(requestDto.getEmail())
        .ifPresent(
            user -> {
              LOGGER.error("User with email {} already exists", requestDto.getEmail());
              throw new EmailAlreadyExistsException(requestDto.getEmail());
            });

    requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

    try {
      restTemplate.postForEntity(
          "http://business-service/users/register", requestDto, Object.class);

      User userDetails = userMapper.userRegisterDtoToUser(requestDto);

      userRepository.save(userDetails);

      String accessToken = jwtUtil.generateToken(userDetails, "ACCESS");
      String refreshToken = jwtUtil.generateToken(userDetails, "REFRESH");

      saveUserToken(userDetails, accessToken, refreshToken);

      return AuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    } catch (Exception ex) {
      LOGGER.error("User was not registered: {}", ex.getMessage(), ex);
      throw new UserNotRegisteredException(
          String.format("User was not registered: %s", ex.getMessage()), ex);
    }
  }

  public AuthResponseDto authenticate(AuthRequestDto request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User userDetails =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String accessToken = jwtUtil.generateToken(userDetails, "ACCESS");
    String refreshToken = jwtUtil.generateToken(userDetails, "REFRESH");
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, accessToken, refreshToken);

    return AuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  private void validateUserRole(UserRegisterDto request) {
    try {
      UserRole.valueOf(request.getRole().name().toUpperCase());
    } catch (IllegalArgumentException ex) {
      LOGGER.error("Invalid role: {}, Ex: {}", request.getRole(), ex.getMessage());
      throw new RoleNotFoundException(request.getRole().name());
    }
  }

  private void revokeAllUserTokens(User user) {
    List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (!validUserTokens.isEmpty()) {
      validUserTokens.forEach(
          token -> {
            token.setExpired(true);
            token.setRevoked(true);
          });
      tokenRepository.saveAll(validUserTokens);
    }
  }

  private void saveUserToken(User user, String accessToken, String refreshToken) {
    Token token =
        Token.builder()
            .user(user)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  public Boolean validateToken(String token) {
    Token validToken =
        tokenRepository
            .findByAccessToken(token)
            .orElseThrow(() -> new TokenNotFoundException(token));

    return !validToken.isRevoked() && !validToken.isExpired();
  }
}
