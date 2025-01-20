package org.project.cursexchange.service;

import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.dto.RequestExchangeDTO;

public interface ExchangeCurrencyService {
    ResponseExchangeDTO exchangeCurrencies(RequestExchangeDTO getConvertedRequestExchangeDTO);
}
