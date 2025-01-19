package org.project.cursexchange.mapper;

import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateMapper {
    public static ExchangeRate mapRowToExchangeRate(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, RoundingMode.HALF_UP);
        Currency baseCurrency = new Currency(resultSet.getInt("BaseCurrencyId"),
                resultSet.getString("BaseCurrencyCode"),
                resultSet.getString("BaseCurrencyName"),
                resultSet.getString("BaseCurrencySign"));

        Currency targetCurrency = new Currency(resultSet.getInt("TargetCurrencyId"),
                resultSet.getString("TargetCurrencyCode"),
                resultSet.getString("TargetCurrencyName"),
                resultSet.getString("TargetCurrencySign"));
        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
