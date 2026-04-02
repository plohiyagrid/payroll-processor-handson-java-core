package com.grid.payroll.app;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.domain.PaySlip;
import com.grid.payroll.processor.PayrollProcessor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo application demonstrating the payroll calculator with various employee scenarios.
 */
public class PayrollDemo {
    public static void main(String[] args) {
        System.out.println("=== Payroll Calculator Demo ===\n");
        
        PayrollProcessor processor = new PayrollProcessor();
        List<Employee> employees = createDemoEmployees();
        
        // Process individual payslips
        System.out.println("--- Individual Pay Slips ---");
        for (Employee employee : employees) {
            BigDecimal hoursOrDays = getHoursOrDaysForDemo(employee);
            PaySlip paySlip = processor.generatePaySlip(employee, hoursOrDays);
            System.out.println(paySlip);
        }
        
        // Process monthly payroll
        System.out.println("\n--- Monthly Payroll Processing ---");
        List<PaySlip> monthlyPaySlips = processor.processMonthlyPayroll(employees);
        System.out.println("Total employees processed: " + monthlyPaySlips.size());
        
        // Summary
        BigDecimal totalGrossPay = monthlyPaySlips.stream()
                .map(PaySlip::getGrossPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = monthlyPaySlips.stream()
                .map(PaySlip::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetPay = monthlyPaySlips.stream()
                .map(PaySlip::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("\n=== Monthly Payroll Summary ===");
        System.out.println("Total Gross Pay: $" + totalGrossPay);
        System.out.println("Total Tax: $" + totalTax);
        System.out.println("Total Net Pay: $" + totalNetPay);
    }
    
    /**
     * Creates demo employees covering all types and deduction combinations.
     */
    private static List<Employee> createDemoEmployees() {
        List<Employee> employees = new ArrayList<>();
        
        // FULL_TIME Employee 1: With all deductions (health insurance, retirement, union dues)
        Employee fullTime1 = new EmployeeBuilder()
                .id("FT001")
                .name("John Smith")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        employees.add(fullTime1);
        
        // FULL_TIME Employee 2: No deductions
        Employee fullTime2 = new EmployeeBuilder()
                .id("FT002")
                .name("Jane Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("3000"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        employees.add(fullTime2);
        
        // PART_TIME Employee 1: Union member
        Employee partTime1 = new EmployeeBuilder()
                .id("PT001")
                .name("Bob Johnson")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(true)
                .hasRetirement(false)
                .build();
        employees.add(partTime1);
        
        // PART_TIME Employee 2: With retirement
        Employee partTime2 = new EmployeeBuilder()
                .id("PT002")
                .name("Alice Williams")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("30"))
                .isUnionMember(false)
                .hasRetirement(true)
                .build();
        employees.add(partTime2);
        
        // CONTRACTOR Employee 1: High daily rate
        Employee contractor1 = new EmployeeBuilder()
                .id("CT001")
                .name("Charlie Brown")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        employees.add(contractor1);
        
        // CONTRACTOR Employee 2: Lower daily rate with union membership
        Employee contractor2 = new EmployeeBuilder()
                .id("CT002")
                .name("Diana Prince")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("150"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        employees.add(contractor2);
        
        return employees;
    }
    
    /**
     * Gets hours or days for demo based on employee type.
     */
    private static BigDecimal getHoursOrDaysForDemo(Employee employee) {
        switch (employee.getEmployeeType()) {
            case FULL_TIME:
                return BigDecimal.ZERO; // Ignored for full-time
            case PART_TIME:
                return new BigDecimal("80"); // 80 hours per month
            case CONTRACTOR:
                return new BigDecimal("20"); // 20 days per month
            default:
                return BigDecimal.ZERO;
        }
    }
}
