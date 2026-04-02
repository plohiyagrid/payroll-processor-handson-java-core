package com.grid.payroll.domain;

import com.grid.payroll.exception.InvalidEmployeeException;
import com.grid.payroll.exception.InvalidPayRateException;
import com.grid.payroll.util.ValidationUtils;

import java.math.BigDecimal;

/**
 * Builder class for constructing Employee objects.
 * Implements Builder Pattern to provide flexible and validated object construction.
 * Custom builder with validation logic (Lombok @Builder doesn't support custom validation easily).
 */
public class EmployeeBuilder {
    private String id;
    private String name;
    private EmployeeType employeeType;
    private BigDecimal payRate;
    private boolean isUnionMember;
    private boolean hasRetirement;
    
    // Fluent setters (Lombok @Setter generates these, but we keep explicit ones for clarity)
    public EmployeeBuilder id(String id) {
        this.id = id;
        return this;
    }

    public EmployeeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public EmployeeBuilder employeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
        return this;
    }

    public EmployeeBuilder payRate(BigDecimal payRate) {
        this.payRate = payRate;
        return this;
    }

    public EmployeeBuilder isUnionMember(boolean isUnionMember) {
        this.isUnionMember = isUnionMember;
        return this;
    }

    public EmployeeBuilder hasRetirement(boolean hasRetirement) {
        this.hasRetirement = hasRetirement;
        return this;
    }

    /**
     * Builds and validates the Employee object.
     *
     * @return a validated Employee instance
     * @throws InvalidEmployeeException if validation fails
     * @throws InvalidPayRateException if pay rate is invalid
     */
    public Employee build() {
        // Validate required fields
        try {
            ValidationUtils.validateNotNullOrEmpty(id, "Employee ID");
            ValidationUtils.validateNotNullOrEmpty(name, "Employee Name");
            
            if (employeeType == null) {
                throw new InvalidEmployeeException("Employee type cannot be null");
            }
            
            if (payRate == null) {
                throw new InvalidPayRateException("Pay rate cannot be null");
            }
            
            ValidationUtils.validatePositive(payRate, "Pay rate");
            
        } catch (IllegalArgumentException e) {
            throw new InvalidEmployeeException("Invalid employee data: " + e.getMessage(), e);
        }
        
        return new Employee(id, name, employeeType, payRate, isUnionMember, hasRetirement);
    }
}
