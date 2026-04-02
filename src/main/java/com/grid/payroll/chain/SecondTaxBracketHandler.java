package com.grid.payroll.chain;

import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;

/**
 * Handler for the second tax bracket: 10% for $1001-$3000.
 */
public class SecondTaxBracketHandler extends TaxBracketHandler {
    
    @Override
    public BigDecimal process(BigDecimal grossPay, BigDecimal accumulatedTax) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return accumulatedTax;
        }
        
        // Calculate taxable amount in this bracket
        // This bracket covers $1001-$3000, which is $2000 range
        BigDecimal bracketRange = PayrollConstants.TAX_BRACKET_2_MAX.subtract(PayrollConstants.TAX_BRACKET_1_MAX);
        BigDecimal taxableAmount = grossPay.min(bracketRange);
        
        // Calculate tax for this bracket (10%)
        BigDecimal taxForThisBracket = taxableAmount.multiply(PayrollConstants.TAX_RATE_10);
        
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
