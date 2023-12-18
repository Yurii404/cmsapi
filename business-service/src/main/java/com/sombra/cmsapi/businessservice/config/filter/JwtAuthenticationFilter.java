package com.sombra.cmsapi.businessservice.config.filter;

import com.sombra.cmsapi.businessservice.entity.AuthUserDetails;
import com.sombra.cmsapi.businessservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final RestTemplate restTemplate;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    if (jwtUtil.isTokenValid(jwt)) {
      userEmail = jwtUtil.extractUsername(jwt);

      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() != null) {
        AuthUserDetails userDetails =
            (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userDetails.getEmail().equals(userEmail)) {
          authenticateUser(userEmail);
        }
      }

      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        authenticateUser(userEmail);
      }
    } else {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }

  private void authenticateUser(String userEmail) {
    AuthUserDetails userDetails =
        restTemplate
            .getForEntity("http://auth-service/user-details/" + userEmail, AuthUserDetails.class)
            .getBody();

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
