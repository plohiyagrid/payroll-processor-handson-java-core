package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.util.PayrollConstants;
import com.grid.payroll.util.ValidationUtils;
import java.math.BigDecimal;

/**
 * Strategy implementation for calculating gross pay for contractors.
 * Contractors are paid daily rate multiplied by days worked.
 */
public class ContractorPayStrategy implements PayCalculationStrategy {
    
    @Override
    public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        if (hoursOrDays == null) {
            throw new IllegalArgumentException("Days cannot be null for contractors");
        }
        
        // Validate days (positive integer)
        ValidationUtils.validateDays(hoursOrDays);
        
        // Calculate: daily rate × days worked
        BigDecimal grossPay = employee.getPayRate().multiply(hoursOrDays);
        
        return grossPay.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
    }
}
