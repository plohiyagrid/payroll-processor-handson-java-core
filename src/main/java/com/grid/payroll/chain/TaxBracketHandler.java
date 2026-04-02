package com.grid.payroll.chain;

import java.math.BigDecimal;

/**
 * Abstract base class for tax bracket handlers in the Chain of Responsibility pattern.
 * Each handler processes a specific tax bracket range.
 */
public abstract class TaxBracketHandler {
    protected TaxBracketHandler nextHandler;

    /**
     * Sets the next handler in the chain.
     *
     * @param nextHandler the next handler
     */
    public void setNext(TaxBracketHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Processes the tax calculation for this bracket and passes remaining amount to next handler.
     *
     * @param grossPay the gross pay amount
     * @param accumulatedTax the tax accumulated so far
     * @return the total tax after processing this bracket and subsequent brackets
     */
    public abstract BigDecimal process(BigDecimal grossPay, BigDecimal accumulatedTax);
}
