package com.grid.payroll.exception;

/**
 * Exception thrown when an employee object is invalid or missing required fields.
 */
public class InvalidEmployeeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidEmployeeException(String message) {
        super(message);
    }

    public InvalidEmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
