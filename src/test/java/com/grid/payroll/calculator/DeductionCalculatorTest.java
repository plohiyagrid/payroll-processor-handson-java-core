package com.grid.payroll.calculator;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.util.PayrollConstants;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for DeductionCalculator.
 */
class DeductionCalculatorTest {
    
    private final DeductionCalculator deductionCalculator = new PayrollDeductionCalculator();
    
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
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("5000"));
        
        assertThat(deductions).hasSize(3);
        assertThat(deductions.get(PayrollConstants.DEDUCTION_HEALTH_INSURANCE))
                .isEqualByComparingTo(PayrollConstants.HEALTH_INSURANCE_AMOUNT);
        assertThat(deductions.get(PayrollConstants.DEDUCTION_RETIREMENT))
                .isEqualByComparingTo(new BigDecimal("250.00")); // 5% of 5000
        assertThat(deductions.get(PayrollConstants.DEDUCTION_UNION_DUES))
                .isEqualByComparingTo(PayrollConstants.UNION_DUES_AMOUNT);
    }
    
    @Test
    void calculateDeductions_ForFullTimeWithNoOptionalDeductions_ReturnsOnlyHealthInsurance() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("5000"));
        
        // Full-time employees always have health insurance
        assertThat(deductions).hasSize(1);
        assertThat(deductions).containsKey(PayrollConstants.DEDUCTION_HEALTH_INSURANCE);
        assertThat(deductions).doesNotContainKey(PayrollConstants.DEDUCTION_RETIREMENT);
        assertThat(deductions).doesNotContainKey(PayrollConstants.DEDUCTION_UNION_DUES);
    }
    
    @Test
    void calculateDeductions_ForPartTime_DoesNotIncludeHealthInsurance() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("2000"));
        
        assertThat(deductions).doesNotContainKey(PayrollConstants.DEDUCTION_HEALTH_INSURANCE);
        assertThat(deductions).containsKey(PayrollConstants.DEDUCTION_RETIREMENT);
        assertThat(deductions).containsKey(PayrollConstants.DEDUCTION_UNION_DUES);
    }
    
    @Test
    void calculateDeductions_ForContractor_DoesNotIncludeHealthInsurance() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("4000"));
        
        assertThat(deductions).doesNotContainKey(PayrollConstants.DEDUCTION_HEALTH_INSURANCE);
        assertThat(deductions).isEmpty();
    }
    
    @Test
    void calculateDeductions_RetirementIsPercentageOfGrossPay() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .hasRetirement(true)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("3000"));
        
        assertThat(deductions.get(PayrollConstants.DEDUCTION_RETIREMENT))
                .isEqualByComparingTo(new BigDecimal("150.00")); // 5% of 3000
    }
    
    @Test
    void calculateDeductions_WithZeroGrossPay_ReturnsZeroRetirement() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .hasRetirement(true)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, BigDecimal.ZERO);
        
        assertThat(deductions.get(PayrollConstants.DEDUCTION_RETIREMENT))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateDeductions_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> deductionCalculator.calculateDeductions(null, new BigDecimal("5000")))
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
        
        assertThatThrownBy(() -> deductionCalculator.calculateDeductions(employee, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateDeductions_RoundsToTwoDecimals() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .hasRetirement(true)
                .build();
        
        Map<String, BigDecimal> deductions = deductionCalculator.calculateDeductions(employee, new BigDecimal("3333.33"));
        
        BigDecimal retirement = deductions.get(PayrollConstants.DEDUCTION_RETIREMENT);
        assertThat(retirement.scale()).isEqualTo(2);
    }
}
