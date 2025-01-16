package org.project.cursexchange.dto;

import org.project.cursexchange.model.Currency;

import java.math.BigDecimal;

public class ExchangeCurrencyDTO {
    private Currency currencyBase;
    private Currency currencyTarget;
    private BigDecimal rate;

    public ExchangeCurrencyDTO(Currency currencyBase, Currency currencyTarget, BigDecimal rate) {
        this.currencyBase = currencyBase;
        this.currencyTarget = currencyTarget;
        this.rate = rate;
    }

    public Currency getCurrencyBase() {
        return currencyBase;
    }

    public void setCurrencyBase(Currency currencyBase) {
        this.currencyBase = currencyBase;
    }

    public Currency getCurrencyTarget() {
        return currencyTarget;
    }

    public void setCurrencyTarget(Currency currencyTarget) {
        this.currencyTarget = currencyTarget;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}