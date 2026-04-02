package com.grid.payroll.chain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TaxBracketHandler abstract class.
 */
class TaxBracketHandlerTest {
    
    @Test
    void setNext_SetsNextHandler() {
        FirstTaxBracketHandler handler1 = new FirstTaxBracketHandler();
        SecondTaxBracketHandler handler2 = new SecondTaxBracketHandler();
        
        handler1.setNext(handler2);
        
        // Verify by processing - if next is set, it should be called
        BigDecimal tax = handler1.process(new BigDecimal("2000"), BigDecimal.ZERO);
        // Should process through both handlers
        assertThat(tax).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void chain_WithAllHandlers_ProcessesCorrectly() {
        FirstTaxBracketHandler first = new FirstTaxBracketHandler();
        SecondTaxBracketHandler second = new SecondTaxBracketHandler();
        ThirdTaxBracketHandler third = new ThirdTaxBracketHandler();
        FourthTaxBracketHandler fourth = new FourthTaxBracketHandler();
        
        first.setNext(second);
        second.setNext(third);
        third.setNext(fourth);
        
        // Process $6000 through entire chain
        BigDecimal tax = first.process(new BigDecimal("6000"), BigDecimal.ZERO);
        
        // Expected: $0 (first $1000) + $200 (10% of $2000) + $400 (20% of $2000) + $300 (30% of $1000) = $900
        assertThat(tax).isEqualByComparingTo(new BigDecimal("900.00"));
    }
}
