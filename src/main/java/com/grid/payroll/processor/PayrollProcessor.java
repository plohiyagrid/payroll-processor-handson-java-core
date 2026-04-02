package com.grid.payroll.processor;

import com.grid.payroll.calculator.DeductionCalculator;
import com.grid.payroll.calculator.PayrollDeductionCalculator;
import com.grid.payroll.calculator.ProgressiveTaxCalculator;
import com.grid.payroll.calculator.TaxCalculator;
import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.domain.PaySlip;
import com.grid.payroll.exception.PayrollProcessingException;
import com.grid.payroll.strategy.ContractorPayStrategy;
import com.grid.payroll.strategy.FullTimePayStrategy;
import com.grid.payroll.strategy.PartTimePayStrategy;
import com.grid.payroll.strategy.PayCalculationStrategy;
import com.grid.payroll.util.PayrollConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main processor for payroll calculations.
 * Provides methods to calculate gross pay, tax, deductions, generate pay slips, and process monthly payroll.
 * Implements Template Method pattern for payroll processing flow.
 */
public class PayrollProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(PayrollProcessor.class);
    
    private final TaxCalculator taxCalculator;
    private final DeductionCalculator deductionCalculator;
    private final Map<EmployeeType, PayCalculationStrategy> payStrategies;
    
    /**
     * Constructs a PayrollProcessor with default calculators and strategies.
     */
    public PayrollProcessor() {
        this.taxCalculator = new ProgressiveTaxCalculator();
        this.deductionCalculator = new PayrollDeductionCalculator();
        this.payStrategies = initializeStrategies();
    }
    
    /**
     * Constructs a PayrollProcessor with custom calculators and strategies (for testing).
     */
    public PayrollProcessor(TaxCalculator taxCalculator, DeductionCalculator deductionCalculator,
                            Map<EmployeeType, PayCalculationStrategy> payStrategies) {
        this.taxCalculator = taxCalculator;
        this.deductionCalculator = deductionCalculator;
        this.payStrategies = payStrategies;
    }
    
    private Map<EmployeeType, PayCalculationStrategy> initializeStrategies() {
        Map<EmployeeType, PayCalculationStrategy> strategies = new HashMap<>();
        strategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        strategies.put(EmployeeType.PART_TIME, new PartTimePayStrategy());
        strategies.put(EmployeeType.CONTRACTOR, new ContractorPayStrategy());
        return strategies;
    }
    
    /**
     * Calculates the gross pay for an employee based on their type and hours/days worked.
     *
     * @param employee the employee
     * @param hoursOrDays hours worked (for PART_TIME) or days worked (for CONTRACTOR)
     * @return the calculated gross pay
     */
    public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        PayCalculationStrategy strategy = payStrategies.get(employee.getEmployeeType());
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for employee type: " + employee.getEmployeeType());
        }
        
        return strategy.calculateGrossPay(employee, hoursOrDays);
    }

    /**
     * Calculates the tax amount based on gross pay using progressive tax brackets.
     *
     * @param grossPay the gross pay amount
     * @return the calculated tax amount
     */
    public BigDecimal calculateTax(BigDecimal grossPay) {
        if (grossPay == null) {
            throw new IllegalArgumentException("Gross pay cannot be null");
        }
        
        return taxCalculator.calculateTax(grossPay);
    }

    /**
     * Calculates all applicable deductions for an employee.
     *
     * @param employee the employee
     * @param grossPay the gross pay amount
     * @return a map of deduction names to their amounts
     */
    public Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        if (grossPay == null) {
            throw new IllegalArgumentException("Gross pay cannot be null");
        }
        
        return deductionCalculator.calculateDeductions(employee, grossPay);
    }

    /**
     * Generates a pay slip for an employee.
     * Template Method: calculate gross → calculate tax → calculate deductions → calculate net.
     *
     * @param employee the employee
     * @param hoursOrDays hours worked (for PART_TIME) or days worked (for CONTRACTOR)
     * @return the generated payslip
     */
    public PaySlip generatePaySlip(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        try {
            // Step 1: Calculate gross pay
            BigDecimal grossPay = calculateGrossPay(employee, hoursOrDays);
            grossPay = grossPay.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            
            // Step 2: Calculate tax
            BigDecimal taxAmount = calculateTax(grossPay);
            taxAmount = taxAmount.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            
            // Step 3: Calculate deductions
            Map<String, BigDecimal> deductions = calculateDeductions(employee, grossPay);
            
            // Step 4: Calculate net pay
            BigDecimal totalDeductions = deductions.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            
            BigDecimal netPay = grossPay.subtract(taxAmount).subtract(totalDeductions);
            
            // Handle negative net pay: set to zero (business decision)
            if (netPay.compareTo(BigDecimal.ZERO) < 0) {
                logger.warn("Net pay is negative for employee {}: {}. Setting to zero.", 
                           employee.getId(), netPay);
                netPay = BigDecimal.ZERO;
            }
            
            netPay = netPay.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
            
            return new PaySlip(employee, grossPay, taxAmount, deductions, netPay);
            
        } catch (Exception e) {
            logger.error("Error generating pay slip for employee {}: {}", employee.getId(), e.getMessage(), e);
            throw new PayrollProcessingException("Failed to generate pay slip for employee: " + employee.getId(), e);
        }
    }

    /**
     * Processes monthly payroll for a list of employees.
     * If one employee fails, others still process (exception handling).
     *
     * @param employeeList the list of employees to process
     * @return a list of generated payslips
     */
    public List<PaySlip> processMonthlyPayroll(List<Employee> employeeList) {
        if (employeeList == null) {
            throw new IllegalArgumentException("Employee list cannot be null");
        }
        
        if (employeeList.isEmpty()) {
            logger.warn("Empty employee list provided for monthly payroll processing");
            return new ArrayList<>();
        }
        
        List<PaySlip> paySlips = new ArrayList<>();
        
        for (Employee employee : employeeList) {
            try {
                // For monthly payroll, use default hours/days based on employee type
                BigDecimal hoursOrDays = getDefaultHoursOrDays(employee);
                PaySlip paySlip = generatePaySlip(employee, hoursOrDays);
                paySlips.add(paySlip);
                logger.info("Successfully processed payroll for employee: {}", employee.getId());
                
            } catch (Exception e) {
                logger.error("Failed to process payroll for employee {}: {}", 
                           employee.getId(), e.getMessage(), e);
                // Continue processing other employees
            }
        }
        
        logger.info("Processed payroll for {}/{} employees", paySlips.size(), employeeList.size());
        return paySlips;
    }
    
    /**
     * Gets default hours or days for monthly payroll based on employee type.
     *
     * @param employee the employee
     * @return default hours or days (null for FULL_TIME)
     */
    private BigDecimal getDefaultHoursOrDays(Employee employee) {
        // For monthly payroll demo, we'll use reasonable defaults
        // In real system, this would come from timesheet/attendance data
        switch (employee.getEmployeeType()) {
            case PART_TIME:
                // Default to 80 hours for part-time (typical monthly hours)
                return new BigDecimal("80");
            case CONTRACTOR:
                // Default to 20 days for contractor (typical working days per month)
                return new BigDecimal("20");
            case FULL_TIME:
            default:
                // FULL_TIME doesn't need hours/days
                return BigDecimal.ZERO;
        }
    }
}
