package com.grid.payroll.domain;

import com.grid.payroll.exception.InvalidEmployeeException;
import com.grid.payroll.exception.InvalidPayRateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for EmployeeBuilder.
 */
class EmployeeBuilderTest {
    
    @Test
    void build_WithValidData_CreatesEmployee() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        assertThat(employee.getId()).isEqualTo("E001");
        assertThat(employee.getName()).isEqualTo("John Doe");
        assertThat(employee.getEmployeeType()).isEqualTo(EmployeeType.FULL_TIME);
        assertThat(employee.getPayRate()).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(employee.isUnionMember()).isTrue();
        assertThat(employee.hasRetirement()).isTrue();
    }
    
    @Test
    void build_WithNullId_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build())
                .isInstanceOf(InvalidEmployeeException.class);
    }
    
    @Test
    void build_WithNullName_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .id("E001")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build())
                .isInstanceOf(InvalidEmployeeException.class);
    }
    
    @Test
    void build_WithNullEmployeeType_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .payRate(new BigDecimal("5000"))
                .build())
                .isInstanceOf(InvalidEmployeeException.class);
    }
    
    @Test
    void build_WithNullPayRate_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .build())
                .isInstanceOf(InvalidPayRateException.class);
    }
    
    @Test
    void build_WithNegativePayRate_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("-1000"))
                .build())
                .isInstanceOf(InvalidEmployeeException.class);
    }
    
    @Test
    void build_WithZeroPayRate_ThrowsException() {
        assertThatThrownBy(() -> new EmployeeBuilder()
                .id("E001")
                .name("John Doe")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(BigDecimal.ZERO)
                .build())
                .isInstanceOf(InvalidEmployeeException.class);
    }
}
