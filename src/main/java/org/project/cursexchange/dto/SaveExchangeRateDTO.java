package org.project.cursexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SaveExchangeRateDTO {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}