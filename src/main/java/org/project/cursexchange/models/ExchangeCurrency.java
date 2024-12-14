package org.project.cursexchange.models;

import java.math.BigDecimal;

public class ExchangeCurrency {
    private long id;
    private int baseCurrencyId;
    private int TargetCurrencyId;

    public ExchangeCurrency(long id, int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        TargetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return TargetCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        TargetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    private BigDecimal rate;
}
