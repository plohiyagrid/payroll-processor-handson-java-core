package com.grid.payroll.exception;

/**
 * Exception thrown when an employee's pay rate is invalid (negative, zero, etc.).
 */
public class InvalidPayRateException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidPayRateException(String message) {
        super(message);
    }

    public InvalidPayRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
