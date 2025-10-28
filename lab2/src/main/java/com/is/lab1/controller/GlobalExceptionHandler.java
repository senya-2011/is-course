package com.is.lab1.controller;

import com.is.lab1.exception.CarCannotBeDeletedException;
import com.is.lab1.exception.CarNotFoundException;
import com.is.lab1.exception.HumanBeingNotFoundException;
import com.is.lab1.exception.ImportProcessingException;
import com.is.lab1.exception.ImportValidationException;
import com.is.lab1.exception.BusinessValidationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((FieldError fe) -> {
      errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>())
          .add(fe.getDefaultMessage());
    });
    return ResponseEntity.badRequest().body(Map.of("errors", errors));
  }

  @ExceptionHandler(CarNotFoundException.class)
  public ResponseEntity<?> handleCarNotFound(CarNotFoundException ex) {
    logger.warn("Car not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(CarCannotBeDeletedException.class)
  public ResponseEntity<?> handleCarCannotBeDeleted(CarCannotBeDeletedException ex) {
    logger.warn("Car cannot be deleted: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(HumanBeingNotFoundException.class)
  public ResponseEntity<?> handleHumanBeingNotFound(HumanBeingNotFoundException ex) {
    logger.warn("HumanBeing not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(ImportValidationException.class)
  public ResponseEntity<?> handleImportValidation(ImportValidationException ex) {
    logger.warn("Import validation failed: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(ImportProcessingException.class)
  public ResponseEntity<?> handleImportProcessing(ImportProcessingException ex) {
    logger.error("Import processing failed: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(BusinessValidationException.class)
  public ResponseEntity<?> handleBusinessValidation(BusinessValidationException ex) {
    logger.warn("Business validation failed: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
    String param = ex.getParameterName();
    logger.warn("Missing request parameter: {}", param);
    return ResponseEntity.badRequest().body(Map.of("message", "Missing parameter: " + param));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAny(Exception ex) {
    logger.error("Unexpected error occurred", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Internal error"));
  }
}
