package com.sombra.cmsapi.businessservice.config.filter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public JwtAuthenticationToken(String token) {
        super(null); // null authorities, as we don't have them in JWT
        this.token = token;
        setAuthenticated(false); // initially, the token is not authenticated
    }

    public JwtAuthenticationToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        setAuthenticated(true); // if authorities are provided, consider the token as authenticated
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null; // In a real scenario, you might have a principal object representing the authenticated user.
    }
}

