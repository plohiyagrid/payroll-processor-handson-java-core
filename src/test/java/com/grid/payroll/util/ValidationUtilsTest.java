package com.grid.payroll.util;

import com.grid.payroll.exception.InvalidDaysException;
import com.grid.payroll.exception.InvalidHoursException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for ValidationUtils.
 */
class ValidationUtilsTest {
    
    @Test
    void validateNotNullOrEmpty_WithNull_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateNotNullOrEmpty(null, "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }
    
    @Test
    void validateNotNullOrEmpty_WithEmpty_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateNotNullOrEmpty("", "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }
    
    @Test
    void validateNotNullOrEmpty_WithWhitespace_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateNotNullOrEmpty("   ", "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }
    
    @Test
    void validateNotNullOrEmpty_WithValidValue_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validateNotNullOrEmpty("valid", "field"));
    }
    
    @Test
    void validatePositive_WithNull_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validatePositive(null, "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void validatePositive_WithZero_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validatePositive(BigDecimal.ZERO, "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }
    
    @Test
    void validatePositive_WithNegative_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validatePositive(new BigDecimal("-10"), "field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }
    
    @Test
    void validatePositive_WithPositive_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositive(new BigDecimal("10"), "field"));
    }
    
    @Test
    void validateHours_WithNull_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateHours(null))
                .isInstanceOf(InvalidHoursException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void validateHours_WithNegative_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateHours(new BigDecimal("-5")))
                .isInstanceOf(InvalidHoursException.class)
                .hasMessageContaining("cannot be negative");
    }
    
    @Test
    void validateHours_WithExceedsMax_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateHours(new BigDecimal("121")))
                .isInstanceOf(InvalidHoursException.class)
                .hasMessageContaining("cannot exceed");
    }
    
    @Test
    void validateHours_WithZero_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validateHours(BigDecimal.ZERO));
    }
    
    @Test
    void validateHours_WithMax_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validateHours(PayrollConstants.MAX_PART_TIME_HOURS));
    }
    
    @Test
    void validateHours_WithValidHours_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validateHours(new BigDecimal("80")));
    }
    
    @Test
    void validateDays_WithNull_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateDays(null))
                .isInstanceOf(InvalidDaysException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void validateDays_WithZero_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateDays(BigDecimal.ZERO))
                .isInstanceOf(InvalidDaysException.class)
                .hasMessageContaining("must be positive");
    }
    
    @Test
    void validateDays_WithNegative_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateDays(new BigDecimal("-5")))
                .isInstanceOf(InvalidDaysException.class)
                .hasMessageContaining("must be positive");
    }
    
    @Test
    void validateDays_WithFractional_ThrowsException() {
        assertThatThrownBy(() -> ValidationUtils.validateDays(new BigDecimal("20.5")))
                .isInstanceOf(InvalidDaysException.class)
                .hasMessageContaining("must be an integer");
    }
    
    @Test
    void validateDays_WithValidInteger_DoesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.validateDays(new BigDecimal("20")));
    }
    
    @Test
    void constructor_IsPrivate_CannotBeInstantiated() {
        // Verify that ValidationUtils cannot be instantiated
        // This is done via reflection to test the private constructor
        java.lang.reflect.Constructor<?>[] constructors = ValidationUtils.class.getDeclaredConstructors();
        assertThat(constructors).hasSize(1);
        assertThat(constructors[0].getModifiers() & java.lang.reflect.Modifier.PRIVATE).isNotZero();
        
        // Try to instantiate via reflection - should throw UnsupportedOperationException
        constructors[0].setAccessible(true);
        assertThatThrownBy(() -> {
            try {
                constructors[0].newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        })
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Utility class cannot be instantiated");
    }
}
