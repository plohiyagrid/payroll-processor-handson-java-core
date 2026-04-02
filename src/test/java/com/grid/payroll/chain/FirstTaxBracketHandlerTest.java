package com.grid.payroll.chain;

import com.grid.payroll.util.PayrollConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FirstTaxBracketHandler.
 */
class FirstTaxBracketHandlerTest {
    
    private FirstTaxBracketHandler handler;
    private SecondTaxBracketHandler nextHandler;
    
    @BeforeEach
    void setUp() {
        handler = new FirstTaxBracketHandler();
        nextHandler = new SecondTaxBracketHandler();
        handler.setNext(nextHandler);
    }
    
    @Test
    void process_WithZeroGrossPay_ReturnsZeroTax() {
        BigDecimal tax = handler.process(BigDecimal.ZERO, BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void process_WithNegativeGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(new BigDecimal("-100"), new BigDecimal("50"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("50"));
    }
    
    @Test
    void process_WithNullGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(null, new BigDecimal("50"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("50"));
    }
    
    @Test
    void process_AtBracketBoundary_ReturnsZeroTax() {
        BigDecimal tax = handler.process(PayrollConstants.TAX_BRACKET_1_MAX, BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void process_BelowBracketBoundary_ReturnsZeroTax() {
        BigDecimal tax = handler.process(new BigDecimal("500"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void process_AboveBracketBoundary_PassesToNextHandler() {
        BigDecimal tax = handler.process(new BigDecimal("2000"), BigDecimal.ZERO);
        // First bracket: $0 tax, remaining $1000 passed to next handler
        // Next handler processes $1000 at 10% = $100
        assertThat(tax).isEqualByComparingTo(new BigDecimal("100.00"));
    }
    
    @Test
    void process_WithNoNextHandler_ReturnsZeroTax() {
        FirstTaxBracketHandler handlerWithoutNext = new FirstTaxBracketHandler();
        BigDecimal tax = handlerWithoutNext.process(new BigDecimal("2000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void process_WithAccumulatedTax_AddsToAccumulated() {
        BigDecimal accumulatedTax = new BigDecimal("50");
        BigDecimal tax = handler.process(new BigDecimal("2000"), accumulatedTax);
        // Should add to accumulated tax
        assertThat(tax).isGreaterThan(accumulatedTax);
    }
}
