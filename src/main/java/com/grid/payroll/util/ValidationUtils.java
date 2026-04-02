package com.grid.payroll.util;

import com.grid.payroll.exception.InvalidDaysException;
import com.grid.payroll.exception.InvalidHoursException;
import java.math.BigDecimal;

/**
 * Utility class for validation operations.
 */
public class ValidationUtils {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates that a string is not null or empty.
     *
     * @param value the value to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotNullOrEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    /**
     * Validates that a BigDecimal is positive.
     *
     * @param value the value to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive, but was: " + value);
        }
    }

    /**
     * Validates that hours are within valid range (0-120).
     *
     * @param hours the hours to validate
     * @throws InvalidHoursException if validation fails
     */
    public static void validateHours(BigDecimal hours) {
        if (hours == null) {
            throw new InvalidHoursException("Hours cannot be null");
        }
        if (hours.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidHoursException("Hours cannot be negative: " + hours);
        }
        if (hours.compareTo(PayrollConstants.MAX_PART_TIME_HOURS) > 0) {
            throw new InvalidHoursException(
                "Hours cannot exceed " + PayrollConstants.MAX_PART_TIME_HOURS + ", but was: " + hours);
        }
        // Check for fractional hours (optional - depends on business rules)
        // For now, we allow fractional hours
    }

    /**
     * Validates that days are positive integers.
     *
     * @param days the days to validate
     * @throws InvalidDaysException if validation fails
     */
    public static void validateDays(BigDecimal days) {
        if (days == null) {
            throw new InvalidDaysException("Days cannot be null");
        }
        if (days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDaysException("Days must be positive, but was: " + days);
        }
        // Check if days is an integer
        if (days.scale() > 0) {
            throw new InvalidDaysException("Days must be an integer, but was: " + days);
        }
    }
}
