package com.is.lab1.exception;

public class CarCannotBeDeletedException extends RuntimeException {
    
    public CarCannotBeDeletedException(String message) {
        super(message);
    }
    
    public CarCannotBeDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
