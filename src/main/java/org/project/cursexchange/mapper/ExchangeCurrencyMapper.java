package org.project.cursexchange.mapper;

import org.project.cursexchange.dao.Dao;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeCurrencyMapper implements RowMapper<ExchangeRate> {
    private final Dao dao;

    public ExchangeCurrencyMapper(Dao dao) {
        this.dao = dao;
    }

    @Override
    public ExchangeRate mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int baseCurrencyId = resultSet.getInt("BaseCurrencyId");
        int targetCurrencyId = resultSet.getInt("TargetCurrencyId");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, RoundingMode.HALF_UP);

        Currency baseCurrency = dao.findById(baseCurrencyId).orElse(null);
        Currency targetCurrency = dao.findById(targetCurrencyId).orElse(null);
        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
