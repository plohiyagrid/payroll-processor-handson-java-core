package com.grid.payroll.calculator;

import com.grid.payroll.chain.FirstTaxBracketHandler;
import com.grid.payroll.chain.FourthTaxBracketHandler;
import com.grid.payroll.chain.SecondTaxBracketHandler;
import com.grid.payroll.chain.TaxBracketHandler;
import com.grid.payroll.chain.ThirdTaxBracketHandler;
import com.grid.payroll.util.PayrollConstants;
import java.math.BigDecimal;

//Implementation of TaxCalculator using Chain of Responsibility pattern.
//Calculates progressive tax based on tax brackets.
public class ProgressiveTaxCalculator implements TaxCalculator {
    
    private final TaxBracketHandler taxChain;
    
    //Constructs a ProgressiveTaxCalculator and sets up the tax bracket chain.
    public ProgressiveTaxCalculator() {
        // Build the chain: First -> Second -> Third -> Fourth
        TaxBracketHandler first = new FirstTaxBracketHandler();
        TaxBracketHandler second = new SecondTaxBracketHandler();
        TaxBracketHandler third = new ThirdTaxBracketHandler();
        TaxBracketHandler fourth = new FourthTaxBracketHandler();
        
        first.setNext(second);
        second.setNext(third);
        third.setNext(fourth);
        
        this.taxChain = first;
    }
    
    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        if (grossPay == null) {
            throw new IllegalArgumentException("Gross pay cannot be null");
        }
        
        if (grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(PayrollConstants.DECIMAL_PLACES, PayrollConstants.ROUNDING_MODE);
        }
        
        // Start the chain with zero accumulated tax
        BigDecimal accumulatedTax = BigDecimal.ZERO;
        return taxChain.process(grossPay, accumulatedTax);
    }
}
