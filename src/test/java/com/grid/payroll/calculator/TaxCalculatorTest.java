package com.grid.payroll.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for TaxCalculator.
 */
class TaxCalculatorTest {
    
    private final TaxCalculator taxCalculator = new ProgressiveTaxCalculator();
    
    @Test
    void calculateTax_WithZeroGrossPay_ReturnsZero() {
        BigDecimal tax = taxCalculator.calculateTax(BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateTax_WithNegativeGrossPay_ReturnsZero() {
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("-100"));
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateTax_WithNullGrossPay_ThrowsException() {
        assertThatThrownBy(() -> taxCalculator.calculateTax(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }
    
    @Test
    void calculateTax_AtFirstBracketBoundary_ReturnsZero() {
        // $1000 should have $0 tax (0%)
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("1000"));
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void calculateTax_InSecondBracket_ReturnsCorrectTax() {
        // $2000 gross = $0 (first $1000) + $100 (10% of $1000) = $100 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("2000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("100.00"));
    }
    
    @Test
    void calculateTax_AtSecondBracketBoundary_ReturnsCorrectTax() {
        // $3000 gross = $0 (first $1000) + $200 (10% of $2000) = $200 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("3000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("200.00"));
    }
    
    @Test
    void calculateTax_InThirdBracket_ReturnsCorrectTax() {
        // $4000 gross = $0 (first $1000) + $200 (10% of $2000) + $200 (20% of $1000) = $400 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("4000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("400.00"));
    }
    
    @Test
    void calculateTax_AtThirdBracketBoundary_ReturnsCorrectTax() {
        // $5000 gross = $0 (first $1000) + $200 (10% of $2000) + $400 (20% of $2000) = $600 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("5000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600.00"));
    }
    
    @Test
    void calculateTax_InFourthBracket_ReturnsCorrectTax() {
        // $6000 gross = $0 (first $1000) + $200 (10% of $2000) + $400 (20% of $2000) + $300 (30% of $1000) = $900 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("6000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("900.00"));
    }
    
    @Test
    void calculateTax_WithLargeAmount_ReturnsCorrectTax() {
        // $10000 gross = $0 (first $1000) + $200 (10% of $2000) + $400 (20% of $2000) + $1500 (30% of $5000) = $2100 tax
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("10000"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("2100.00"));
    }
    
    @Test
    void calculateTax_RoundsToTwoDecimals() {
        BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("3500"));
        assertThat(tax.scale()).isEqualTo(2);
    }
}
