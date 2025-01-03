package org.project.cursexchange.dao;

import org.project.cursexchange.dto.ExchangeCurrencyDTO;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeCurrencyDao {
    Optional<ExchangeCurrency> findCurrencyExchangeById(Currency baseCurrency, Currency targetCurrency) throws SQLException;
    boolean saveCurrencyExchange(ExchangeCurrencyDTO exchangeCurrencyDto) throws SQLException;
    boolean updateRateCurrencyExchange(ExchangeCurrency exchangeCurrency, String value ) throws SQLException;
    List<ExchangeCurrency> findCurrencyExchangeByTargetCode(String code) throws SQLException;
    List<ExchangeCurrency> findAllCurrencyExchange() throws SQLException;
}
