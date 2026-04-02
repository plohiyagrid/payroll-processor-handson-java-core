package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.exception.InvalidHoursException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PartTimePayStrategy.
 */
class PartTimePayStrategyTest {
    
    private final PartTimePayStrategy strategy = new PartTimePayStrategy();
    
    @Test
    void calculateGrossPay_WithValidHours_ReturnsCorrectAmount() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, new BigDecimal("80"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("2000.00"));
    }
    
    @Test
    void calculateGrossPay_WithZeroHours_ReturnsZero() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, BigDecimal.ZERO);
        
        assertThat(grossPay).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateGrossPay_WithMaxHours_ReturnsCorrectAmount() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("20"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, new BigDecimal("120"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("2400.00"));
    }
    
    @Test
    void calculateGrossPay_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> strategy.calculateGrossPay(null, new BigDecimal("80")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateGrossPay_WithNullHours_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateGrossPay_WithNegativeHours_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, new BigDecimal("-10")))
                .isInstanceOf(InvalidHoursException.class);
    }
    
    @Test
    void calculateGrossPay_WithExceedsMaxHours_ThrowsException() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        assertThatThrownBy(() -> strategy.calculateGrossPay(employee, new BigDecimal("121")))
                .isInstanceOf(InvalidHoursException.class);
    }
}
