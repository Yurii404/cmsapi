package com.sombra.cmsapi.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String token) {
        super(String.format("Token %s does not exist", token));
    }
}
