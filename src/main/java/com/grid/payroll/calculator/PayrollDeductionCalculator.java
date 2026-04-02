package com.grid.payroll.calculator;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//Implementation of DeductionCalculator.
//Calculates all applicable deductions for an employee.

public class PayrollDeductionCalculator implements DeductionCalculator {
    
    @Override
    public Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        if (grossPay == null) {
            throw new IllegalArgumentException("Gross pay cannot be null");
        }
        
        Map<String, BigDecimal> deductions = new HashMap<>();
        
        // Health Insurance: $150 flat (only for FULL_TIME employees)
        if (employee.getEmployeeType() == EmployeeType.FULL_TIME) {
            BigDecimal healthInsurance = PayrollConstants.HEALTH_INSURANCE_AMOUNT
                    .setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            deductions.put(PayrollConstants.DEDUCTION_HEALTH_INSURANCE, healthInsurance);
        }
        
        // Retirement Contribution: 5% of gross pay (only if hasRetirement = true)
        if (employee.hasRetirement()) {
            BigDecimal retirement = grossPay.multiply(PayrollConstants.RETIREMENT_RATE)
                    .setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            deductions.put(PayrollConstants.DEDUCTION_RETIREMENT, retirement);
        }
        
        // Union Dues: $50 flat (only if isUnionMember = true)
        if (employee.isUnionMember()) {
            BigDecimal unionDues = PayrollConstants.UNION_DUES_AMOUNT
                    .setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            deductions.put(PayrollConstants.DEDUCTION_UNION_DUES, unionDues);
        }
        
        return deductions;
    }
}
