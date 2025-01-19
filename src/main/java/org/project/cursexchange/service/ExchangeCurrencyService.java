package org.project.cursexchange.service;

import org.project.cursexchange.dto.ExchangeCalculationRateDTO;
import org.project.cursexchange.dto.SaveExchangeRateDTO;

public interface ExchangeCurrencyService {
    ExchangeCalculationRateDTO getExchangeCurrencyWithConvertedAmount(SaveExchangeRateDTO saveExchangeRateDTO);
}
