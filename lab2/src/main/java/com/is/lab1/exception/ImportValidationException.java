package com.is.lab1.exception;

public class ImportValidationException extends RuntimeException {

  public ImportValidationException(String message) {
    super(message);
  }

  public ImportValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}


