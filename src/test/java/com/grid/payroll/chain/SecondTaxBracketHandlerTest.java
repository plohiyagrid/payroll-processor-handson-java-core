package com.grid.payroll.chain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SecondTaxBracketHandler.
 */
class SecondTaxBracketHandlerTest {
    
    private SecondTaxBracketHandler handler;
    private ThirdTaxBracketHandler nextHandler;
    
    @BeforeEach
    void setUp() {
        handler = new SecondTaxBracketHandler();
        nextHandler = new ThirdTaxBracketHandler();
        handler.setNext(nextHandler);
    }
    
    @Test
    void process_WithZeroGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(BigDecimal.ZERO, new BigDecimal("100"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("100"));
    }
    
    @Test
    void process_WithNullGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(null, new BigDecimal("100"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("100"));
    }
    
    @Test
    void process_AtBracketStart_CalculatesCorrectTax() {
        // $1000 passed from first bracket, should calculate 10% of $1000 = $100
        BigDecimal tax = handler.process(new BigDecimal("1000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(new BigDecimal("100.00"));
    }
    
    @Test
    void process_AtBracketEnd_CalculatesCorrectTax() {
        // $2000 passed from first bracket (full bracket range)
        BigDecimal tax = handler.process(new BigDecimal("2000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(new BigDecimal("200.00"));
    }
    
    @Test
    void process_AboveBracket_PassesRemainderToNext() {
        // $3000 passed, bracket covers $2000, remainder $1000 goes to next handler
        BigDecimal tax = handler.process(new BigDecimal("3000"), BigDecimal.ZERO);
        // $200 (10% of $2000) + next handler processes $1000 at 20% = $200
        assertThat(tax).isEqualByComparingTo(new BigDecimal("400.00"));
    }
    
    @Test
    void process_WithNoNextHandler_ReturnsBracketTaxOnly() {
        SecondTaxBracketHandler handlerWithoutNext = new SecondTaxBracketHandler();
        BigDecimal tax = handlerWithoutNext.process(new BigDecimal("2000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(new BigDecimal("200.00"));
    }
    
    @Test
    void process_WithExactBracketAmount_NoRemainder_DoesNotCallNext() {
        // Process exactly the bracket range, so no remainder
        BigDecimal tax = handler.process(new BigDecimal("2000"), BigDecimal.ZERO);
        // Should return $200 (10% of $2000) without calling next handler
        assertThat(tax).isEqualByComparingTo(new BigDecimal("200.00"));
    }
    
    @Test
    void process_WithRemainderButNoNextHandler_ReturnsBracketTaxOnly() {
        SecondTaxBracketHandler handlerWithoutNext = new SecondTaxBracketHandler();
        // Process more than bracket range, but no next handler
        BigDecimal tax = handlerWithoutNext.process(new BigDecimal("3000"), BigDecimal.ZERO);
        // Should only process $2000 at 10% = $200, ignore remainder
        assertThat(tax).isEqualByComparingTo(new BigDecimal("200.00"));
    }
    
    @Test
    void process_WithNegativeGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(new BigDecimal("-100"), new BigDecimal("50"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("50"));
    }
    
    @Test
    void process_WithAccumulatedTax_AddsToAccumulated() {
        BigDecimal tax = handler.process(new BigDecimal("1000"), new BigDecimal("50"));
        // Should add $100 (10% of $1000) to accumulated $50 = $150
        assertThat(tax).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
