package com.is.lab1.exception;

public class HumanBeingCannotBeDeletedException extends RuntimeException {
  public HumanBeingCannotBeDeletedException(String message) {
    super(message);
  }
}
