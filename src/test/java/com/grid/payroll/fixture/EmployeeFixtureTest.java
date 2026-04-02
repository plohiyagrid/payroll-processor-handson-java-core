package com.grid.payroll.fixture;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmployeeFixture.
 */
class EmployeeFixtureTest {
    
    @Test
    void createFullTimeEmployee_ReturnsValidEmployee() {
        Employee employee = EmployeeFixture.createFullTimeEmployee();
        
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeType()).isEqualTo(EmployeeType.FULL_TIME);
        assertThat(employee.getId()).isEqualTo("FT001");
    }
    
    @Test
    void createPartTimeEmployee_ReturnsValidEmployee() {
        Employee employee = EmployeeFixture.createPartTimeEmployee();
        
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeType()).isEqualTo(EmployeeType.PART_TIME);
        assertThat(employee.getId()).isEqualTo("PT001");
    }
    
    @Test
    void createContractorEmployee_ReturnsValidEmployee() {
        Employee employee = EmployeeFixture.createContractorEmployee();
        
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeType()).isEqualTo(EmployeeType.CONTRACTOR);
        assertThat(employee.getId()).isEqualTo("CT001");
    }
}
