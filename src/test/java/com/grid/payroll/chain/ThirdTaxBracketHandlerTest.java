package com.grid.payroll.chain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ThirdTaxBracketHandler.
 */
class ThirdTaxBracketHandlerTest {
    
    private ThirdTaxBracketHandler handler;
    private FourthTaxBracketHandler nextHandler;
    
    @BeforeEach
    void setUp() {
        handler = new ThirdTaxBracketHandler();
        nextHandler = new FourthTaxBracketHandler();
        handler.setNext(nextHandler);
    }
    
    @Test
    void process_WithZeroGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(BigDecimal.ZERO, new BigDecimal("300"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("300"));
    }
    
    @Test
    void process_WithNullGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(null, new BigDecimal("300"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("300"));
    }
    
    @Test
    void process_AtBracketStart_CalculatesCorrectTax() {
        BigDecimal tax = handler.process(new BigDecimal("1000"), new BigDecimal("200"));
        // 20% of $1000 = $200, plus accumulated $200 = $400
        assertThat(tax).isEqualByComparingTo(new BigDecimal("400.00"));
    }
    
    @Test
    void process_AtBracketEnd_CalculatesCorrectTax() {
        BigDecimal tax = handler.process(new BigDecimal("2000"), new BigDecimal("200"));
        // 20% of $2000 = $400, plus accumulated $200 = $600
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600.00"));
    }
    
    @Test
    void process_AboveBracket_PassesRemainderToNext() {
        BigDecimal tax = handler.process(new BigDecimal("3000"), new BigDecimal("200"));
        // $400 (20% of $2000) + accumulated $200 = $600
        // Next handler processes $1000 at 30% = $300
        // Total = $900
        assertThat(tax).isEqualByComparingTo(new BigDecimal("900.00"));
    }
    
    @Test
    void process_WithExactBracketAmount_NoRemainder_DoesNotCallNext() {
        // Process exactly the bracket range, so no remainder
        BigDecimal tax = handler.process(new BigDecimal("2000"), new BigDecimal("200"));
        // Should return $600 (20% of $2000 = $400 + accumulated $200) without calling next handler
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600.00"));
    }
    
    @Test
    void process_WithRemainderButNoNextHandler_ReturnsBracketTaxOnly() {
        ThirdTaxBracketHandler handlerWithoutNext = new ThirdTaxBracketHandler();
        // Process more than bracket range, but no next handler
        BigDecimal tax = handlerWithoutNext.process(new BigDecimal("3000"), new BigDecimal("200"));
        // Should only process $2000 at 20% = $400, plus accumulated $200 = $600
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600.00"));
    }
    
    @Test
    void process_WithNegativeGrossPay_ReturnsAccumulatedTax() {
        BigDecimal tax = handler.process(new BigDecimal("-100"), new BigDecimal("300"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("300"));
    }
    
    @Test
    void process_WithNoNextHandler_ReturnsBracketTaxOnly() {
        ThirdTaxBracketHandler handlerWithoutNext = new ThirdTaxBracketHandler();
        BigDecimal tax = handlerWithoutNext.process(new BigDecimal("2000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(new BigDecimal("400.00"));
    }
}
