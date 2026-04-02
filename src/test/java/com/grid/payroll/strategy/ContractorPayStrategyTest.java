package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.exception.InvalidDaysException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for ContractorPayStrategy.
 */
class ContractorPayStrategyTest {
    
    private final ContractorPayStrategy strategy = new ContractorPayStrategy();
    
    @Test
    void calculateGrossPay_WithValidDays_ReturnsCorrectAmount() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, new BigDecimal("20"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("4000.00"));
    }
    
    @Test
    void calculateGrossPay_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> strategy.calculateGrossPay(null, new BigDecimal("20")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateGrossPay_WithNullDays_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateGrossPay_WithZeroDays_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, BigDecimal.ZERO))
                .isInstanceOf(InvalidDaysException.class);
    }
    
    @Test
    void calculateGrossPay_WithNegativeDays_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, new BigDecimal("-5")))
                .isInstanceOf(InvalidDaysException.class);
    }
    
    @Test
    void calculateGrossPay_WithFractionalDays_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, new BigDecimal("20.5")))
                .isInstanceOf(InvalidDaysException.class)
                .hasMessageContaining("must be an integer");
    }
}
