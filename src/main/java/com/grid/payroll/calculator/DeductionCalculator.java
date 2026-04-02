package com.grid.payroll.calculator;

import com.grid.payroll.domain.Employee;
import java.math.BigDecimal;
import java.util.Map;

//Interface for calculating deductions for an employee.
public interface DeductionCalculator {
    /**
     * Calculates all applicable deductions for an employee based on their gross pay.
     *
     * @param employee the employee for whom to calculate deductions
     * @param grossPay the gross pay amount
     * @return a map of deduction names to their amounts
     */
    Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay);
}
