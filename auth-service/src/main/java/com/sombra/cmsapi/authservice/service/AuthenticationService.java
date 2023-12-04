package com.sombra.cmsapi.authservice.service;

import com.sombra.cmsapi.authservice.config.JwtService;
import com.sombra.cmsapi.authservice.dto.AuthenticationRequestDto;
import com.sombra.cmsapi.authservice.dto.AuthenticationResponseDto;
import com.sombra.cmsapi.authservice.dto.RegisterRequestDto;
import com.sombra.cmsapi.authservice.entity.Token;
import com.sombra.cmsapi.authservice.enums.Role;
import com.sombra.cmsapi.authservice.entity.User;
import com.sombra.cmsapi.authservice.enums.TokenType;
import com.sombra.cmsapi.authservice.exception.EmailAlreadyExistsException;
import com.sombra.cmsapi.authservice.exception.RoleNotFoundException;
import com.sombra.cmsapi.authservice.repository.TokenRepository;
import com.sombra.cmsapi.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto register(RegisterRequestDto request) {
        //Do check if the role is valid
        Role userRole = getRoleOrThrow(request);

        //Do check if the user with the email already exist
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new EmailAlreadyExistsException(request.getEmail());
        });

        User user = User.builder()
                .firstname(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    private static Role getRoleOrThrow(RegisterRequestDto request) {
        Role userRole;
        try {
            userRole = Role.valueOf(request.getRole().toUpperCase());
        }catch (IllegalArgumentException ex){
            log.error("Invalid role: {}, Ex: {}", request.getRole(), ex.getMessage());
            throw new RoleNotFoundException(request.getRole());
        }
        return userRole;
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    private void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(!validUserTokens.isEmpty()){
            validUserTokens.forEach(token ->{
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
