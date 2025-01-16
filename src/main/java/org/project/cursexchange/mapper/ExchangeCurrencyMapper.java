package org.project.cursexchange.mapper;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeCurrencyMapper implements RowMapper<ExchangeRate> {
    private final CurrencyDao currencyDao;

    public ExchangeCurrencyMapper(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    public ExchangeRate mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int baseCurrencyId = resultSet.getInt("BaseCurrencyId");
        int targetCurrencyId = resultSet.getInt("TargetCurrencyId");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, RoundingMode.HALF_UP);

        Currency baseCurrency = currencyDao.findById(baseCurrencyId).orElse(null);
        Currency targetCurrency = currencyDao.findById(targetCurrencyId).orElse(null);
        return new ExchangeRate(id, baseCurrency, targetCurrency,rate);
    }
}
