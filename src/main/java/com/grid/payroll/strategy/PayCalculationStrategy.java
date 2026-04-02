package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating gross pay based on employee type.
 * Implements Strategy Pattern to allow different calculation logic for each employee type.
 */
public interface PayCalculationStrategy {
    /**
     * Calculates the gross pay for an employee based on their type and hours/days worked.
     *
     * @param employee the employee for whom to calculate gross pay
     * @param hoursOrDays the hours worked (for PART_TIME) or days worked (for CONTRACTOR)
     * @return the calculated gross pay
     */
    BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays);
}
