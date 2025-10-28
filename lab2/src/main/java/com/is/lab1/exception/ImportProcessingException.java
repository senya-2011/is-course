package com.is.lab1.exception;

public class ImportProcessingException extends RuntimeException {

  public ImportProcessingException(String message) {
    super(message);
  }

  public ImportProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}


