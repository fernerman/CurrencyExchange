package org.project.cursexchange.service;

import org.project.cursexchange.dto.ExchangeCalculationDTO;
import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeCurrencyService {
    ExchangeCalculationDTO getExchangeCurrencyWithConvertedAmount(String currencyBaseCode, String currencyTargetCode, String amount);
}
