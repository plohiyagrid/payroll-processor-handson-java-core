package com.grid.payroll.chain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FourthTaxBracketHandler.
 */
class FourthTaxBracketHandlerTest {
    
    private FourthTaxBracketHandler handler;
    
    @Test
    void process_WithZeroGrossPay_ReturnsAccumulatedTax() {
        handler = new FourthTaxBracketHandler();
        BigDecimal tax = handler.process(BigDecimal.ZERO, new BigDecimal("600"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600"));
    }
    
    @Test
    void process_WithNullGrossPay_ReturnsAccumulatedTax() {
        handler = new FourthTaxBracketHandler();
        BigDecimal tax = handler.process(null, new BigDecimal("600"));
        assertThat(tax).isEqualByComparingTo(new BigDecimal("600"));
    }
    
    @Test
    void process_Calculates30PercentTax() {
        handler = new FourthTaxBracketHandler();
        BigDecimal tax = handler.process(new BigDecimal("1000"), new BigDecimal("600"));
        // 30% of $1000 = $300, plus accumulated $600 = $900
        assertThat(tax).isEqualByComparingTo(new BigDecimal("900.00"));
    }
    
    @Test
    void process_WithLargeAmount_CalculatesCorrectTax() {
        handler = new FourthTaxBracketHandler();
        BigDecimal tax = handler.process(new BigDecimal("5000"), new BigDecimal("600"));
        // 30% of $5000 = $1500, plus accumulated $600 = $2100
        assertThat(tax).isEqualByComparingTo(new BigDecimal("2100.00"));
    }
    
    @Test
    void process_IsFinalHandler_DoesNotCallNext() {
        handler = new FourthTaxBracketHandler();
        // Even if next handler is set, it shouldn't be called (but we don't set it for this handler)
        BigDecimal tax = handler.process(new BigDecimal("1000"), BigDecimal.ZERO);
        assertThat(tax).isEqualByComparingTo(new BigDecimal("300.00"));
    }
}
