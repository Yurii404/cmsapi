package com.sombra.cmsapi.businessservice.config;

import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.exception.NotAllowedOperationException;
import com.sombra.cmsapi.businessservice.exception.WrongSearchFieldException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(WrongSearchFieldException.class)
  public ResponseEntity<String> handleWrongSearchFieldException(WrongSearchFieldException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse("Validation error");
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotAllowedOperationException.class)
  public ResponseEntity<String> handleNotAllowedOperationException(
      NotAllowedOperationException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
