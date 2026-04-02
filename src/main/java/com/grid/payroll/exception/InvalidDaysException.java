package com.grid.payroll.exception;

/**
 * Exception thrown when days worked are invalid (negative, zero, fractional, etc.).
 */
public class InvalidDaysException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidDaysException(String message) {
        super(message);
    }

    public InvalidDaysException(String message, Throwable cause) {
        super(message, cause);
    }
}
