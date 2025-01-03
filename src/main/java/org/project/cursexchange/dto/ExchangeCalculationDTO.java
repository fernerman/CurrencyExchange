package org.project.cursexchange.dto;

import org.project.cursexchange.models.Currency;

import java.math.BigDecimal;

public class ExchangeCalculationDTO {
    private Currency currencyBase;
    private Currency currencyTarget;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeCalculationDTO(Currency currencyBase, Currency currencyTarget, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.currencyBase = currencyBase;
        this.currencyTarget = currencyTarget;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
