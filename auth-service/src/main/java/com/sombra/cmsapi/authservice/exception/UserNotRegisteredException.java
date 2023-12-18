package com.sombra.cmsapi.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotRegisteredException extends RuntimeException {
  public UserNotRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
