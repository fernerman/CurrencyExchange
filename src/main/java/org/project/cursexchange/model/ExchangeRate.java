package org.project.cursexchange.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class ExchangeRate {
    private long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
