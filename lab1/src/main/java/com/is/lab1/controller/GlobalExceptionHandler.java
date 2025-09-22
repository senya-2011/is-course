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

/**
 * Глобальный обработчик исключений для приложения.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Обрабатывает исключения валидации.
   *
   * @param ex исключение
   * @return ответ
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((FieldError fe) -> {
      errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>())
          .add(fe.getDefaultMessage());
    });
    return ResponseEntity.badRequest().body(Map.of(
        "status", HttpStatus.BAD_REQUEST.value(),
        "errors", errors
    ));
  }

  /**
   * Обрабатывает исключения когда что-то не найдено.
   *
   * @param ex исключение
   * @return ответ
   */
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", ex.getMessage()));
  }

  /**
   * Обрабатывает исключения неправильных аргументов.
   *
   * @param ex исключение
   * @return ответ
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", ex.getMessage()));
  }

  /**
   * Обрабатывает любые другие исключения.
   *
   * @param ex исключение
   * @return ответ
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAny(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "message", "Internal error"));
  }
}
