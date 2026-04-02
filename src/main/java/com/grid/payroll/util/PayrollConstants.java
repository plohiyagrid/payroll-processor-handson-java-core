package com.grid.payroll.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Constants used throughout the payroll system.
 */
public class PayrollConstants {
    
    // Tax bracket thresholds
    public static final BigDecimal TAX_BRACKET_1_MAX = new BigDecimal("1000");
    public static final BigDecimal TAX_BRACKET_2_MAX = new BigDecimal("3000");
    public static final BigDecimal TAX_BRACKET_3_MAX = new BigDecimal("5000");
    
    // Tax rates
    public static final BigDecimal TAX_RATE_0 = BigDecimal.ZERO;
    public static final BigDecimal TAX_RATE_10 = new BigDecimal("0.10");
    public static final BigDecimal TAX_RATE_20 = new BigDecimal("0.20");
    public static final BigDecimal TAX_RATE_30 = new BigDecimal("0.30");
    
    // Deduction amounts
    public static final BigDecimal HEALTH_INSURANCE_AMOUNT = new BigDecimal("150");
    public static final BigDecimal UNION_DUES_AMOUNT = new BigDecimal("50");
    public static final BigDecimal RETIREMENT_RATE = new BigDecimal("0.05");
    
    // Limits
    public static final BigDecimal MAX_PART_TIME_HOURS = new BigDecimal("120");
    
    // Rounding
    public static final int DECIMAL_PLACES = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    // Deduction names
    public static final String DEDUCTION_HEALTH_INSURANCE = "Health Insurance";
    public static final String DEDUCTION_RETIREMENT = "Retirement Contribution";
    public static final String DEDUCTION_UNION_DUES = "Union Dues";
    
    private PayrollConstants() {
        // Utility class - prevent instantiation
    }
}
