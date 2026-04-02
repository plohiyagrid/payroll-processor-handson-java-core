package com.grid.payroll.processor;

import com.grid.payroll.domain.Employee;
import com.grid.payroll.domain.EmployeeBuilder;
import com.grid.payroll.domain.EmployeeType;
import com.grid.payroll.domain.PaySlip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PayrollProcessor.
 */
class PayrollProcessorIntegrationTest {
    
    private PayrollProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new PayrollProcessor();
    }
    
    @Test
    void processMonthlyPayroll_WithMixedEmployeeTypes_ProcessesAll() {
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
        
        List<Employee> employees = Arrays.asList(fullTime, partTime, contractor);
        List<PaySlip> paySlips = processor.processMonthlyPayroll(employees);
        
        assertThat(paySlips).hasSize(3);
        assertThat(paySlips).extracting(PaySlip::getEmployee)
                .containsExactly(fullTime, partTime, contractor);
    }
    
    @Test
    void processMonthlyPayroll_WithAllDeductionCombinations_CoversAllScenarios() {
        Employee withAllDeductions = new EmployeeBuilder()
                .id("E001")
                .name("All Deductions")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        Employee withNoDeductions = new EmployeeBuilder()
                .id("E002")
                .name("No Deductions")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .isUnionMember(false)
                .hasRetirement(false)
                .build();
        
        List<Employee> employees = Arrays.asList(withAllDeductions, withNoDeductions);
        List<PaySlip> paySlips = processor.processMonthlyPayroll(employees);
        
        assertThat(paySlips).hasSize(2);
        
        PaySlip slip1 = paySlips.get(0);
        assertThat(slip1.getDeductions()).hasSize(3); // Health, Retirement, Union
        
        PaySlip slip2 = paySlips.get(1);
        assertThat(slip2.getDeductions()).isEmpty(); // No deductions for part-time
    }
    
    @Test
    void processMonthlyPayroll_EndToEndFlow_CalculatesCorrectly() {
        Employee employee = new EmployeeBuilder()
                .id("E001")
                .name("Test Employee")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .isUnionMember(true)
                .hasRetirement(true)
                .build();
        
        List<PaySlip> paySlips = processor.processMonthlyPayroll(Arrays.asList(employee));
        
        assertThat(paySlips).hasSize(1);
        PaySlip paySlip = paySlips.get(0);
        
        // Verify calculations
        assertThat(paySlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(paySlip.getTaxAmount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(paySlip.getDeductions()).isNotEmpty();
        assertThat(paySlip.getNetPay()).isLessThan(paySlip.getGrossPay());
        assertThat(paySlip.getNetPay()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void processMonthlyPayroll_WithEmptyList_ReturnsEmptyList() {
        List<PaySlip> paySlips = processor.processMonthlyPayroll(Arrays.asList());
        assertThat(paySlips).isEmpty();
    }
    
    @Test
    void generatePaySlip_ForEachEmployeeType_WorksCorrectly() {
        // Full-time
        Employee fullTime = new EmployeeBuilder()
                .id("FT001")
                .name("Full Time")
                .employeeType(EmployeeType.FULL_TIME)
                .payRate(new BigDecimal("5000"))
                .build();
        PaySlip fullTimeSlip = processor.generatePaySlip(fullTime, BigDecimal.ZERO);
        assertThat(fullTimeSlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("5000.00"));
        
        // Part-time
        Employee partTime = new EmployeeBuilder()
                .id("PT001")
                .name("Part Time")
                .employeeType(EmployeeType.PART_TIME)
                .payRate(new BigDecimal("25"))
                .build();
        PaySlip partTimeSlip = processor.generatePaySlip(partTime, new BigDecimal("80"));
        assertThat(partTimeSlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("2000.00"));
        
        // Contractor
        Employee contractor = new EmployeeBuilder()
                .id("CT001")
                .name("Contractor")
                .employeeType(EmployeeType.CONTRACTOR)
                .payRate(new BigDecimal("200"))
                .build();
        PaySlip contractorSlip = processor.generatePaySlip(contractor, new BigDecimal("20"));
        assertThat(contractorSlip.getGrossPay()).isEqualByComparingTo(new BigDecimal("4000.00"));
    }
}

