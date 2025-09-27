package com.is.lab1.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((FieldError fe) -> {
      errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>())
          .add(fe.getDefaultMessage());
    });
    return ResponseEntity.badRequest().body(Map.of("errors", errors));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAny(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Internal error"));
  }
}
