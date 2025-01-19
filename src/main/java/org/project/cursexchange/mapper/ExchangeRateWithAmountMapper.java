package org.project.cursexchange.mapper;

import org.project.cursexchange.dto.ConvertAmountExchangeRateDTO;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;

public class ExchangeRateWithAmountMapper {
    public static ConvertAmountExchangeRateDTO toDTO(ExchangeRate exchangeRate,
                                                     BigDecimal rate,
                                                     BigDecimal amount) {
        return new ConvertAmountExchangeRateDTO(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                rate,
                amount,
                rate.multiply(amount)
        );
    }
}
