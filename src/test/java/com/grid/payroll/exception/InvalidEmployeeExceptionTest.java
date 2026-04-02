package com.grid.payroll.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidEmployeeException.
 */
class InvalidEmployeeExceptionTest {
    
    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Invalid employee data";
        InvalidEmployeeException exception = new InvalidEmployeeException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
    
    @Test
    void constructor_WithMessageAndCause_CreatesException() {
        String message = "Invalid employee data";
        Throwable cause = new IllegalArgumentException("Root cause");
        InvalidEmployeeException exception = new InvalidEmployeeException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
