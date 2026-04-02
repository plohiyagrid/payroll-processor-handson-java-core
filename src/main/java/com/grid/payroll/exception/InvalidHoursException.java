package com.grid.payroll.exception;

/**
 * Exception thrown when hours worked are invalid (negative, exceed maximum, etc.).
 */
public class InvalidHoursException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidHoursException(String message) {
        super(message);
    }

    public InvalidHoursException(String message, Throwable cause) {
        super(message, cause);
    }
}
