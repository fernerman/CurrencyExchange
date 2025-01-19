package org.project.cursexchange.service;

import org.project.cursexchange.dto.ConvertAmountExchangeRateDTO;
import org.project.cursexchange.dto.AmountExchangeRatesDTO;

public interface ExchangeCurrencyService {
    ConvertAmountExchangeRateDTO getExchangeCurrencyWithConvertedAmount(AmountExchangeRatesDTO getConvertedAmountExchangeRatesDTO);
}
