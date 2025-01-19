package org.project.cursexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.cursexchange.model.Currency;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeCalculationRateDTO {
    private Currency currencyBase;
    private Currency currencyTarget;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
