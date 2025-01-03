package org.project.cursexchange.service;

import org.project.cursexchange.dto.ExchangeCalculationDTO;
import org.project.cursexchange.models.ExchangeCurrency;

import java.util.Optional;

public interface  ExchangeCurrencyService {
    Optional<ExchangeCurrency> getExchangeCurrency(String currencyBaseCode, String currencyTargetCode);
    ExchangeCurrency addExchangeCurrency(String baseCurrencyCode,String targetCurrencyCode, String rate);
    ExchangeCurrency updateExchangeCurrency(String baseCurrencyCode,String targetCurrencyCode, String rate);
    ExchangeCalculationDTO getExchangeCurrencyWithConvertedAmount(String currencyBaseCode, String currencyTargetCode, String amount);

}
