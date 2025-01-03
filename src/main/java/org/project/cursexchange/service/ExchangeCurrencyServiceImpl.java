package org.project.cursexchange.service;

import org.project.cursexchange.models.ExchangeCurrency;

public interface  ExchangeCurrencyService {
    ExchangeCurrency getExchangeCurrency(String currencyBaseCode,String currencyTargetCode);
}
