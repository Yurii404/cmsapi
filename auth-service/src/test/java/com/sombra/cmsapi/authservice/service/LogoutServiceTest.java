package com.sombra.cmsapi.authservice.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.authservice.entity.Token;
import com.sombra.cmsapi.authservice.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private Authentication authentication;
  @Mock private TokenRepository tokenRepository;

  @InjectMocks private LogoutService logoutService;

  @Test
  void Should_Logout_When_ValidaData() {
    String validToken = "validToken";
    Token storedToken = new Token();
    storedToken.setAccessToken(validToken);
    storedToken.setExpired(false);
    storedToken.setRevoked(false);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

    when(tokenRepository.findByAccessToken(validToken)).thenReturn(Optional.of(storedToken));
    ArgumentCaptor<Token> argumentCaptor = ArgumentCaptor.forClass(Token.class);

    logoutService.logout(request, response, authentication);

    verify(tokenRepository).save(argumentCaptor.capture());
    assertTrue(argumentCaptor.getValue().isExpired());
    assertTrue(argumentCaptor.getValue().isRevoked());
  }

  @Test
  void Should_NeverSave_When_InvalidToken() {
    when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

    logoutService.logout(request, response, authentication);

    verify(tokenRepository, never()).save(any(Token.class));
  }
}
