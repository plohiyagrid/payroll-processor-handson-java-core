package com.grid.payroll.calculator;

import java.math.BigDecimal;


//Interface for tax calculation.
//Uses Chain of Responsibility pattern for progressive tax bracket calculation.

public interface TaxCalculator {
    /**
     * Calculates the tax amount based on gross pay using progressive tax brackets.
     *
     * @param grossPay the gross pay amount
     * @return the calculated tax amount
     */
    BigDecimal calculateTax(BigDecimal grossPay);
}
