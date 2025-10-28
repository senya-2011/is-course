package com.is.lab1.exception;

public class BusinessValidationException extends RuntimeException {

  public BusinessValidationException(String message) {
    super(message);
  }

  public BusinessValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}


