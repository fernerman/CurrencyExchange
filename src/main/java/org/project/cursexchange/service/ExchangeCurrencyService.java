package org.project.cursexchange.service;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.ExchangeRequestDto;
import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.exception.ExchangeRateNotFound;

import java.sql.SQLException;
import java.util.Optional;

public class ExchangeCurrencyService {
    private static final String DEFAULT_CURRENCY_CODE = "USD";
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public ResponseExchangeDTO exchangeCurrencies(ExchangeRequestDto dto) throws SQLException {
        Optional<ResponseExchangeDTO> exchangeResponseDTO = exchangeRateDao.findDirectExchangeRate(dto);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }
        exchangeResponseDTO = exchangeRateDao.findReversedExchangeRate(dto);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }

        exchangeResponseDTO = exchangeRateDao.findIntermediateExchangeRate(dto, DEFAULT_CURRENCY_CODE);
        if (exchangeResponseDTO.isPresent()) {
            return exchangeResponseDTO.get();
        }
        throw new ExchangeRateNotFound();
    }
}
