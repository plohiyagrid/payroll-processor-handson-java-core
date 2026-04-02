package com.grid.payroll.chain;

import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;

/**
 * Handler for the first tax bracket: 0% for first $1000.
 */
public class FirstTaxBracketHandler extends TaxBracketHandler {
    
    @Override
    public BigDecimal process(BigDecimal grossPay, BigDecimal accumulatedTax) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return accumulatedTax;
        }
        
        // Calculate taxable amount in this bracket (min of grossPay and bracket max)
        BigDecimal taxableAmount = grossPay.min(PayrollConstants.TAX_BRACKET_1_MAX);
        
        // Calculate tax for this bracket (0%)
        BigDecimal taxForThisBracket = taxableAmount.multiply(PayrollConstants.TAX_RATE_0);
        
        // Accumulate tax
        BigDecimal newAccumulatedTax = accumulatedTax.add(taxForThisBracket);
        
        // Calculate remaining amount to process
        BigDecimal remainingAmount = grossPay.subtract(taxableAmount);
        
        // If there's remaining amount, pass to next handler
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0 && nextHandler != null) {
            return nextHandler.process(remainingAmount, newAccumulatedTax);
        }
        
        return newAccumulatedTax.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
    }
}
