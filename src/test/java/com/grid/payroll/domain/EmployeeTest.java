package com.grid.payroll.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Employee class.
 */
class EmployeeTest {
    
    @Test
    void getters_ReturnCorrectValues() {
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
    void getters_WithFalseFlags_ReturnFalse() {
        Employee employee = new EmployeeBuilder()
                .id("E002")
                .name("Jane Smith")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        
        assertThat(employee.isUnionMember()).isFalse();
        assertThat(employee.hasRetirement()).isFalse();
    }
    
    @Test
    void getters_ForAllEmployeeTypes_WorkCorrectly() {
        Employee fullTime = new EmployeeBuilder()
                .id("FT001")
                .name("Full Time")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        
        Employee partTime = new EmployeeBuilder()
                .id("PT001")
                .name("Part Time")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        
        Employee contractor = new EmployeeBuilder()
                .id("CT001")
                .name("Contractor")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        
        assertThat(fullTime.getEmployeeType()).isEqualTo(EmployeeType.FULL_TIME);
        assertThat(partTime.getEmployeeType()).isEqualTo(EmployeeType.PART_TIME);
        assertThat(contractor.getEmployeeType()).isEqualTo(EmployeeType.CONTRACTOR);
    }
}
