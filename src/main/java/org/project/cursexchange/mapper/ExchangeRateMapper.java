package org.project.cursexchange.mapper;

import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateMapper {
    public static ExchangeRate mapRowToExchangeRate(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        BigDecimal rate = resultSet.getBigDecimal(4).setScale(2, RoundingMode.HALF_UP);
        Currency baseCurrency = new Currency(resultSet.getInt(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8));

        Currency targetCurrency = new Currency(resultSet.getInt(9),
                resultSet.getString(10),
                resultSet.getString(11),
                resultSet.getString(12));

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
