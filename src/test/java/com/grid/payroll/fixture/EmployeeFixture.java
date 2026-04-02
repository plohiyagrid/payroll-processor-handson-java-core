package com.grid.payroll.fixture;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import java.math.BigDecimal;

/**
 * Test fixture for creating Employee objects in tests.
 */
public class EmployeeFixture {
    
    /**
     * Creates a sample full-time employee.
     */
    public static Employee createFullTimeEmployee() {
        return new EmployeeBuilder()
                .id("FT001")
                .name("Full Time Employee")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
    }

    /**
     * Creates a sample part-time employee.
     */
    public static Employee createPartTimeEmployee() {
        return new EmployeeBuilder()
                .id("PT001")
                .name("Part Time Employee")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
    }

    /**
     * Creates a sample contractor employee.
     */
    public static Employee createContractorEmployee() {
        return new EmployeeBuilder()
                .id("CT001")
                .name("Contractor Employee")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
    }
}

