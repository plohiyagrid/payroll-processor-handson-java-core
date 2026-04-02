package com.grid.payroll.strategy;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for FullTimePayStrategy.
 */
class FullTimePayStrategyTest {
    
    private final FullTimePayStrategy strategy = new FullTimePayStrategy();
    
    @Test
    void calculateGrossPay_WithValidEmployee_ReturnsPayRate() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, BigDecimal.ZERO);
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("5000.00"));
    }
    
    @Test
    void calculateGrossPay_IgnoresHoursOrDays() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("3000"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, new BigDecimal("100"));
        
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("3000.00"));
    }
    
    @Test
    void calculateGrossPay_WithNullEmployee_ThrowsException() {
        assertThatThrownBy(() -> strategy.calculateGrossPay(null, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateGrossPay_RoundsToTwoDecimals() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000.999"))
                .build();
        
        BigDecimal grossPay = strategy.calculateGrossPay(employee, BigDecimal.ZERO);
        
        assertThat(grossPay.scale()).isEqualTo(2);
        assertThat(grossPay).isEqualByComparingTo(new BigDecimal("5001.00"));
    }
}
