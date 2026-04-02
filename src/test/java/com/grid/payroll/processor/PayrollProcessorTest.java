package com.grid.payroll.processor;

import com.grid.payroll.calculator.DeductionCalculator;
import com.grid.payroll.calculator.PayrollDeductionCalculator;
import com.grid.payroll.calculator.ProgressiveTaxCalculator;
import com.grid.payroll.calculator.TaxCalculator;
import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.domain.PaySlip;
import com.grid.payroll.exception.PayrollProcessingException;
import com.grid.payroll.strategy.FullTimePayStrategy;
import com.grid.payroll.strategy.PartTimePayStrategy;
import com.grid.payroll.strategy.PayCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PayrollProcessor.
 */
class PayrollProcessorTest {
    
    private PayrollProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new PayrollProcessor();
    }
    
    @Test
    void calculateGrossPay_ForFullTime_ReturnsPayRate() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        BigDecimal grossPay = processor.calculateGrossPay(employee, BigDecimal.ZERO);
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("5000.00"));
    }
    
    @Test
    void calculateGrossPay_ForPartTime_ReturnsRateTimesHours() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        BigDecimal grossPay = processor.calculateGrossPay(employee, new BigDecimal("80"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("2000.00"));
    }
    
    @Test
    void calculateGrossPay_ForContractor_ReturnsRateTimesDays() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        BigDecimal grossPay = processor.calculateGrossPay(employee, new BigDecimal("20"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("4000.00"));
    }
    
    @Test
    void calculateTax_WithValidGrossPay_ReturnsTax() {
        BigDecimal tax = processor.calculateTax(new BigDecimal("5000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600.00"));
    }
    
    @Test
    void calculateTax_WithZeroGrossPay_ReturnsZero() {
        BigDecimal tax = processor.calculateTax(BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateDeductions_ForFullTimeWithAllDeductions_ReturnsAllDeductions() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        Map<String, BigDecimal> deductions = processor.calculateDeductions(employee, new BigDecimal("5000"));
        
        assertThat(deductions).hasSize(3);
    }
    
    @Test
    void generatePaySlip_ForFullTimeEmployee_GeneratesCorrectPaySlip() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, BigDecimal.ZERO);
        
        assertThat(paySlip.getEmployee()).isEqualTo(employee);
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(paySlip.getTaxAmount()).isEqualByComparingTo(new BigDecimal("600.00"));
        assertThat(paySlip.getDeductions()).hasSize(3);
        
        // Net pay = 5000 - 600 (tax) - 150 (health) - 250 (retirement) - 50 (union) = 3950
        assertThat(paySlip.getNetPay()).isEqualByComparingTo(new BigDecimal("3950.00"));
    }
    
    @Test
    void generatePaySlip_ForPartTimeEmployee_GeneratesCorrectPaySlip() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(true)
                .hasRetirement(false)
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, new BigDecimal("80"));
        
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(paySlip.getTaxAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        // Should not have health insurance
        assertThat(paySlip.getDeductions()).doesNotContainKey("Health Insurance");
        assertThat(paySlip.getDeductions()).containsKey("Union Dues");
    }
    
    @Test
    void generatePaySlip_WithNegativeNetPay_SetsToZero() {
        // Create scenario where deductions exceed gross - tax
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("100")) // Very low pay
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, BigDecimal.ZERO);
        
        // Net pay should be zero (not negative)
        assertThat(paySlip.getNetPay()).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void generatePaySlip_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> processor.generatePaySlip(null, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void processMonthlyPayroll_WithValidEmployees_ReturnsPaySlips() {
        Employee employee1 = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Employee employee2 = new EmployeeBuilder()
                .id("E002")
                .name("Jane Smith")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        java.util.List<Employee> employees = java.util.Arrays.asList(employee1, employee2);
        
        java.util.List<PaySlip> paySlips = processor.processMonthlyPayroll(employees);
        
        assertThat(paySlips).hasSize(2);
    }
    
    @Test
    void processMonthlyPayroll_WithEmptyList_ReturnsEmptyList() {
        java.util.List<PaySlip> paySlips = processor.processMonthlyPayroll(java.util.Collections.emptyList());
        
        assertThat(paySlips).isEmpty();
    }
    
    @Test
    void processMonthlyPayroll_WithNullList_ThrowsException() {
        assertThatThrownBy(() -> processor.processMonthlyPayroll(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void processMonthlyPayroll_WithOneInvalidEmployee_ContinuesProcessing() {
        Employee validEmployee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        // This will fail during processing (invalid hours)
        Employee invalidEmployee = new EmployeeBuilder()
                .id("E002")
                .name("Jane Smith")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        java.util.List<Employee> employees = java.util.Arrays.asList(validEmployee, invalidEmployee);
        
        // Should process valid employee and skip invalid one
        java.util.List<PaySlip> paySlips = processor.processMonthlyPayroll(employees);
        
        // At least the valid employee should be processed
        assertThat(paySlips.size()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void calculateGrossPay_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> processor.calculateGrossPay(null, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateTax_WithNullGrossPay_ThrowsException() {
        assertThatThrownBy(() -> processor.calculateTax(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateDeductions_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> processor.calculateDeductions(null, new BigDecimal("5000")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateDeductions_WithNullGrossPay_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        assertThatThrownBy(() -> processor.calculateDeductions(employee, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void generatePaySlip_WithZeroGrossPay_HandlesCorrectly() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, BigDecimal.ZERO);
        
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(paySlip.getTaxAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(paySlip.getNetPay()).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void generatePaySlip_WithNoDeductions_CalculatesCorrectly() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, new BigDecimal("80"));
        
        assertThat(paySlip.getDeductions()).isEmpty();
        assertThat(paySlip.getNetPay()).isEqualByComparingTo(
                paySlip.getGrossPay().subtract(paySlip.getTaxAmount()));
    }
    
    @Test
    void generatePaySlip_WithExceptionDuringCalculation_ThrowsPayrollProcessingException() {
        // Create a processor with a strategy that throws exception
        Map<EmployeeType, PayCalculationStrategy> strategies = new HashMap<>();
        strategies.put(EmployeeType.FULL_TIME, new PayCalculationStrategy() {
            @Override
            public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
                throw new RuntimeException("Test exception");
            }
        });
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                strategies
        );
        
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        assertThatThrownBy(() -> customProcessor.generatePaySlip(employee, BigDecimal.ZERO))
                .isInstanceOf(PayrollProcessingException.class)
                .hasMessageContaining("Failed to generate pay slip");
    }
    
    @Test
    void processMonthlyPayroll_WithAllEmployeesFailing_ReturnsEmptyList() {
        // Create employees that will fail processing
        Employee invalidEmployee1 = new EmployeeBuilder()
                .id("E001")
                .name("Invalid 1")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        Employee invalidEmployee2 = new EmployeeBuilder()
                .id("E002")
                .name("Invalid 2")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        // Create processor that will fail for these employees
        Map<EmployeeType, PayCalculationStrategy> strategies = new HashMap<>();
        strategies.put(EmployeeType.PART_TIME, new PayCalculationStrategy() {
            @Override
            public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
                throw new RuntimeException("Processing failed");
            }
        });
        strategies.put(EmployeeType.CONTRACTOR, new PayCalculationStrategy() {
            @Override
            public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
                throw new RuntimeException("Processing failed");
            }
        });
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                strategies
        );
        
        List<Employee> employees = Arrays.asList(invalidEmployee1, invalidEmployee2);
        List<PaySlip> paySlips = customProcessor.processMonthlyPayroll(employees);
        
        // Should return empty list but not throw exception
        assertThat(paySlips).isEmpty();
    }
    
    @Test
    void processMonthlyPayroll_WithMixedSuccessAndFailure_ProcessesSuccessfulOnes() {
        Employee validEmployee = new EmployeeBuilder()
                .id("E001")
                .name("Valid")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Employee invalidEmployee = new EmployeeBuilder()
                .id("E002")
                .name("Invalid")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        // Create processor that fails for PART_TIME but succeeds for FULL_TIME
        Map<EmployeeType, PayCalculationStrategy> strategies = new HashMap<>();
        strategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        strategies.put(EmployeeType.PART_TIME, new PayCalculationStrategy() {
            @Override
            public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
                throw new RuntimeException("Processing failed");
            }
        });
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                strategies
        );
        
        List<Employee> employees = Arrays.asList(validEmployee, invalidEmployee);
        List<PaySlip> paySlips = customProcessor.processMonthlyPayroll(employees);
        
        // Should process valid employee only
        assertThat(paySlips).hasSize(1);
        assertThat(paySlips.get(0).getEmployee()).isEqualTo(validEmployee);
    }
    
    @Test
    void constructor_WithCustomDependencies_UsesProvidedDependencies() {
        TaxCalculator customTaxCalculator = new TaxCalculator() {
            @Override
            public BigDecimal calculateTax(BigDecimal grossPay) {
                return new BigDecimal("100"); // Fixed tax
            }
        };
        
        DeductionCalculator customDeductionCalculator = new DeductionCalculator() {
            @Override
            public Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay) {
                return new HashMap<>();
            }
        };
        
        Map<EmployeeType, PayCalculationStrategy> strategies = new HashMap<>();
        strategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                customTaxCalculator,
                customDeductionCalculator,
                strategies
        );
        
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        PaySlip paySlip = customProcessor.generatePaySlip(employee, BigDecimal.ZERO);
        
        // Should use custom tax calculator (fixed $100 tax)
        assertThat(paySlip.getTaxAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        // Should use custom deduction calculator (no deductions)
        assertThat(paySlip.getDeductions()).isEmpty();
    }
    
    @Test
    void generatePaySlip_WithRoundingEdgeCases_HandlesCorrectly() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("33.333")) // Will create rounding scenarios
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, new BigDecimal("3"));
        
        // All values should be rounded to 2 decimal places
        assertThat(paySlip.getGrossPay().scale()).isLessThanOrEqualTo(2);
        assertThat(paySlip.getTaxAmount().scale()).isLessThanOrEqualTo(2);
        assertThat(paySlip.getNetPay().scale()).isLessThanOrEqualTo(2);
    }
    
    @Test
    void generatePaySlip_ForContractor_GeneratesCorrectPaySlip() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        PaySlip paySlip = processor.generatePaySlip(employee, new BigDecimal("20"));
        
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(paySlip.getTaxAmount()).isGreaterThan(BigDecimal.ZERO);
        // Contractors don't get health insurance
        assertThat(paySlip.getDeductions()).doesNotContainKey("Health Insurance");
    }
    
    @Test
    void calculateGrossPay_WithMissingStrategy_ThrowsException() {
        // Create processor with incomplete strategies map (only FULL_TIME)
        Map<EmployeeType, PayCalculationStrategy> incompleteStrategies = new HashMap<>();
        incompleteStrategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        // Missing PART_TIME and CONTRACTOR strategies
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                incompleteStrategies
        );
        
        Employee partTimeEmployee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        assertThatThrownBy(() -> customProcessor.calculateGrossPay(partTimeEmployee, new BigDecimal("80")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No strategy found for employee type: PART_TIME");
    }
    
    @Test
    void calculateGrossPay_WithMissingStrategyForContractor_ThrowsException() {
        // Create processor with incomplete strategies map (only FULL_TIME and PART_TIME)
        Map<EmployeeType, PayCalculationStrategy> incompleteStrategies = new HashMap<>();
        incompleteStrategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        incompleteStrategies.put(EmployeeType.PART_TIME, new PartTimePayStrategy());
        // Missing CONTRACTOR strategy
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                incompleteStrategies
        );
        
        Employee contractorEmployee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThatThrownBy(() -> customProcessor.calculateGrossPay(contractorEmployee, new BigDecimal("20")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No strategy found for employee type: CONTRACTOR");
    }
    
    @Test
    void generatePaySlip_WithMissingStrategy_ThrowsPayrollProcessingException() {
        // Create processor with incomplete strategies map
        Map<EmployeeType, PayCalculationStrategy> incompleteStrategies = new HashMap<>();
        incompleteStrategies.put(EmployeeType.FULL_TIME, new FullTimePayStrategy());
        // Missing PART_TIME strategy
        
        PayrollProcessor customProcessor = new PayrollProcessor(
                new ProgressiveTaxCalculator(),
                new PayrollDeductionCalculator(),
                incompleteStrategies
        );
        
        Employee partTimeEmployee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        // generatePaySlip should catch the IllegalArgumentException and wrap it in PayrollProcessingException
        assertThatThrownBy(() -> customProcessor.generatePaySlip(partTimeEmployee, new BigDecimal("80")))
                .isInstanceOf(PayrollProcessingException.class)
                .hasMessageContaining("Failed to generate pay slip");
    }
}
