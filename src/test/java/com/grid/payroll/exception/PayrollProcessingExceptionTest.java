package com.grid.payroll.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PayrollProcessingException.
 */
class PayrollProcessingExceptionTest {
    
    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Failed to process payroll";
        PayrollProcessingException exception = new PayrollProcessingException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
    
    @Test
    void constructor_WithMessageAndCause_CreatesException() {
        String message = "Failed to process payroll";
        Throwable cause = new IllegalArgumentException("Root cause");
        PayrollProcessingException exception = new PayrollProcessingException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
