package com.grid.payroll.chain;

import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;

/**
 * Handler for the fourth tax bracket: 30% for above $5000.
 * This is the final bracket, so it processes all remaining amount.
 */
public class FourthTaxBracketHandler extends TaxBracketHandler {
    
    @Override
    public BigDecimal process(BigDecimal grossPay, BigDecimal accumulatedTax) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return accumulatedTax;
        }
        
        // This is the final bracket, so process all remaining amount
        // Calculate tax for this bracket (30%)
        BigDecimal taxForThisBracket = grossPay.multiply(PayrollConstants.TAX_RATE_30);
        
        // Accumulate tax
        BigDecimal newAccumulatedTax = accumulatedTax.add(taxForThisBracket);
        
        return newAccumulatedTax.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
    }
}
