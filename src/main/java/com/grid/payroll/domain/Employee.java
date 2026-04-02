package com.grid.payroll.domain;

import java.math.BigDecimal;

/**
 * Represents an employee in the payroll system.
 * This class is immutable and should be constructed using the EmployeeBuilder.
 */
public class Employee {
    private final String id;
    private final String name;
    private final EmployeeType employeeType;
    private final BigDecimal payRate;
    private final boolean isUnionMember;
    private final boolean hasRetirement;

    // Package-private constructor for builder
    Employee(String id, String name, EmployeeType employeeType, 
             BigDecimal payRate, boolean isUnionMember, boolean hasRetirement) {
        this.id = id;
        this.name = name;
        this.employeeType = employeeType;
        this.payRate = payRate;
        this.isUnionMember = isUnionMember;
        this.hasRetirement = hasRetirement;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public boolean isUnionMember() {
        return isUnionMember;
    }

    public boolean hasRetirement() {
        return hasRetirement;
    }
}
