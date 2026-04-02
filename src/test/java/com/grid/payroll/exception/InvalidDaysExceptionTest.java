package com.grid.payroll.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidDaysException.
 */
class InvalidDaysExceptionTest {
    
    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Days must be positive";
        InvalidDaysException exception = new InvalidDaysException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
    
    @Test
    void constructor_WithMessageAndCause_CreatesException() {
        String message = "Days must be positive";
        Throwable cause = new IllegalArgumentException("Root cause");
        InvalidDaysException exception = new InvalidDaysException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
