package org.project.cursexchange.service;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.RequestExchangeDTO;
import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;

import java.util.Optional;

public class ExchangeCurrencyService {
    //by default
    private static final String CURRENCY_CODE = "USD";
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public ResponseExchangeDTO exchangeCurrencies(RequestExchangeDTO dto) {
        Optional<ResponseExchangeDTO> exchangeResponseDTO = exchangeRateDao.findDirectExchangeRate(dto);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }
        exchangeResponseDTO = exchangeRateDao.findReversedExchangeRate(dto);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }

        exchangeResponseDTO = exchangeRateDao.findIntermediateExchangeRate(dto, CURRENCY_CODE);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }
        throw new CurrencyExchangeNotFound();
    }
}
