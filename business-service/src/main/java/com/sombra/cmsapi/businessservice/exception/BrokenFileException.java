package com.sombra.cmsapi.businessservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BrokenFileException extends RuntimeException {
  public BrokenFileException(String message) {
    super(message);
  }
}
