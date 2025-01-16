package org.project.cursexchange.dao;

import org.project.cursexchange.dto.ExchangeCurrencyDTO;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeCurrencyDao {
    Optional<ExchangeRate> findCurrencyExchangeById(Currency baseCurrency, Currency targetCurrency) throws SQLException;
    boolean saveCurrencyExchange(ExchangeCurrencyDTO exchangeCurrencyDto) throws SQLException;
    boolean updateRateCurrencyExchange(ExchangeRate exchangeRate, String value ) throws SQLException;
    List<ExchangeRate> findCurrencyExchangeByTargetCode(String code) throws SQLException;
    List<ExchangeRate> findAllCurrencyExchange() throws SQLException;
}
