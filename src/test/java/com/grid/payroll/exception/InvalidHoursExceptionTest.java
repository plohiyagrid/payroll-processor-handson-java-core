package com.grid.payroll.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidHoursException.
 */
class InvalidHoursExceptionTest {
    
    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Hours cannot exceed 120";
        InvalidHoursException exception = new InvalidHoursException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
    
    @Test
    void constructor_WithMessageAndCause_CreatesException() {
        String message = "Hours cannot exceed 120";
        Throwable cause = new IllegalArgumentException("Root cause");
        InvalidHoursException exception = new InvalidHoursException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
