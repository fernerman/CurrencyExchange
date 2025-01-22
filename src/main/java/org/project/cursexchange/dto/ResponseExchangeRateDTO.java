package org.project.cursexchange.dto;

import lombok.Getter;
import lombok.Setter;
import org.project.cursexchange.model.Currency;

import java.math.BigDecimal;

@Getter
@Setter
public class ResponseExchangeRateDTO {
    private long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    public ResponseExchangeRateDTO(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}

