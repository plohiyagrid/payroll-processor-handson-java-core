package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;

/**
 * Strategy implementation for calculating gross pay for full-time employees.
 * Full-time employees receive a fixed monthly salary regardless of hours worked.
 */
public class FullTimePayStrategy implements PayCalculationStrategy {
    
    @Override
    public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        // For full-time employees, payRate is the monthly salary
        // hoursOrDays parameter is ignored
        return employee.getPayRate()
                .setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
    }
}
