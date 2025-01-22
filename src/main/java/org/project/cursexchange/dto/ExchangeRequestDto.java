package org.project.cursexchange.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExchangeRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
    private BigDecimal amount;
    public ExchangeRequestDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        this.amount = amount;
        this.targetCurrencyCode = targetCurrencyCode;
        this.baseCurrencyCode = baseCurrencyCode;
    }
}

