package com.grid.payroll.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PaySlip class.
 */
class PaySlipTest {
    
    @Test
    void getters_ReturnCorrectValues() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Map<String, BigDecimal> deductions = new HashMap<>();
        deductions.put("Health Insurance", new BigDecimal("150"));
        
        PaySlip paySlip = new PaySlip(
                employee,
                new BigDecimal("5000.00"),
                new BigDecimal("600.00"),
                deductions,
                new BigDecimal("4250.00")
        );
        
        assertThat(paySlip.getEmployee()).isEqualTo(employee);
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(paySlip.getTaxAmount()).isEqualByComparingTo(new BigDecimal("600.00"));
        assertThat(paySlip.getDeductions()).hasSize(1);
        assertThat(paySlip.getNetPay()).isEqualByComparingTo(new BigDecimal("4250.00"));
    }
    
    @Test
    void getDeductions_ReturnsImmutableMap() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Map<String, BigDecimal> deductions = new HashMap<>();
        deductions.put("Health Insurance", new BigDecimal("150"));
        
        PaySlip paySlip = new PaySlip(
                employee,
                new BigDecimal("5000.00"),
                new BigDecimal("600.00"),
                deductions,
                new BigDecimal("4250.00")
        );
        
        assertThatThrownBy(() -> paySlip.getDeductions().put("New", BigDecimal.ONE))
                .isInstanceOf(UnsupportedOperationException.class);
    }
    
    @Test
    void getDeductions_WithNullMap_ReturnsEmptyMap() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        PaySlip paySlip = new PaySlip(
                employee,
                new BigDecimal("5000.00"),
                new BigDecimal("600.00"),
                null,
                new BigDecimal("4400.00")
        );
        
        assertThat(paySlip.getDeductions()).isEmpty();
    }
    
    @Test
    void toString_ContainsAllInformation() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Map<String, BigDecimal> deductions = new HashMap<>();
        deductions.put("Health Insurance", new BigDecimal("150"));
        
        PaySlip paySlip = new PaySlip(
                employee,
                new BigDecimal("5000.00"),
                new BigDecimal("600.00"),
                deductions,
                new BigDecimal("4250.00")
        );
        
        String toString = paySlip.toString();
        
        assertThat(toString).contains("E001");
        assertThat(toString).contains("John Doe");
        assertThat(toString).contains("FULL_TIME");
        assertThat(toString).contains("5000.00");
        assertThat(toString).contains("600.00");
        assertThat(toString).contains("Health Insurance");
        assertThat(toString).contains("4250.00");
    }
    
    @Test
    void toString_WithNoDeductions_ShowsNone() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        PaySlip paySlip = new PaySlip(
                employee,
                new BigDecimal("2000.00"),
                new BigDecimal("100.00"),
                new HashMap<>(),
                new BigDecimal("1900.00")
        );
        
        String toString = paySlip.toString();
        assertThat(toString).contains("None");
    }
}
