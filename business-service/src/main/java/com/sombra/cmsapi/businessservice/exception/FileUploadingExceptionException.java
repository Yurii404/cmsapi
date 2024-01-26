package com.sombra.cmsapi.businessservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadingExceptionException extends RuntimeException {
  public FileUploadingExceptionException(String message) {
    super(message);
  }
}
