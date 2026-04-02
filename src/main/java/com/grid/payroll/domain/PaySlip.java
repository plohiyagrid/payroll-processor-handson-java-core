package com.grid.payroll.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a payslip for an employee containing gross pay, tax, deductions, and net pay.
 * This class is immutable.
 */
public class PaySlip {
    private final Employee employee;
    private final BigDecimal grossPay;
    private final BigDecimal taxAmount;
    private final Map<String, BigDecimal> deductions;
    private final BigDecimal netPay;

    public PaySlip(Employee employee, BigDecimal grossPay, BigDecimal taxAmount,
                   Map<String, BigDecimal> deductions, BigDecimal netPay) {
        this.employee = employee;
        this.grossPay = grossPay;
        this.taxAmount = taxAmount;
        // Make deductions map immutable
        this.deductions = deductions != null ? 
                Collections.unmodifiableMap(deductions) : Collections.emptyMap();
        this.netPay = netPay;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getGrossPay() {
        return grossPay;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public Map<String, BigDecimal> getDeductions() {
        return deductions;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Pay Slip ===\n");
        sb.append("Employee ID: ").append(employee.getId()).append("\n");
        sb.append("Employee Name: ").append(employee.getName()).append("\n");
        sb.append("Employee Type: ").append(employee.getEmployeeType()).append("\n");
        sb.append("Gross Pay: $").append(grossPay).append("\n");
        sb.append("Tax Amount: $").append(taxAmount).append("\n");
        sb.append("Deductions:\n");
        if (deductions.isEmpty()) {
            sb.append("  None\n");
        } else {
            deductions.forEach((name, amount) -> 
                sb.append("  - ").append(name).append(": $").append(amount).append("\n"));
        }
        BigDecimal totalDeductions = deductions.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sb.append("Total Deductions: $").append(totalDeductions).append("\n");
        sb.append("Net Pay: $").append(netPay).append("\n");
        sb.append("================\n");
        return sb.toString();
    }
}
