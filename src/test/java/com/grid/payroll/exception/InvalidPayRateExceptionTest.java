package com.grid.payroll.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidPayRateException.
 */
class InvalidPayRateExceptionTest {
    
    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Pay rate cannot be negative";
        InvalidPayRateException exception = new InvalidPayRateException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
    
    @Test
    void constructor_WithMessageAndCause_CreatesException() {
        String message = "Pay rate cannot be negative";
        Throwable cause = new IllegalArgumentException("Root cause");
        InvalidPayRateException exception = new InvalidPayRateException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
