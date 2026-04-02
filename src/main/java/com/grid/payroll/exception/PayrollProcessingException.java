package com.grid.payroll.exception;

/**
 * Exception thrown when payroll processing fails.
 */
public class PayrollProcessingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public PayrollProcessingException(String message) {
        super(message);
    }

    public PayrollProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
