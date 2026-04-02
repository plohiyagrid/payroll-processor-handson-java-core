package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.util.PayrollConstants;
import com.grid.payroll.util.ValidationUtils;
import java.math.BigDecimal;

/**
 * Strategy implementation for calculating gross pay for part-time employees.
 * Part-time employees are paid hourly rate multiplied by hours worked (max 120 hours/month).
 */
public class PartTimePayStrategy implements PayCalculationStrategy {
    
    @Override
    public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        if (hoursOrDays == null) {
            throw new IllegalArgumentException("Hours cannot be null for part-time employees");
        }
        
        // Validate hours (0-120)
        ValidationUtils.validateHours(hoursOrDays);
        
        // Calculate: hourly rate × hours worked
        BigDecimal grossPay = employee.getPayRate().multiply(hoursOrDays);
        
        return grossPay.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
    }
}
