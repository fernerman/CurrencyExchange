package org.project.cursexchange.mappers;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeCurrencyMapper implements RowMapper<ExchangeCurrency> {
    private final CurrencyDao currencyDao;

    public ExchangeCurrencyMapper(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    public ExchangeCurrency mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int baseCurrencyId = resultSet.getInt("BaseCurrencyId");
        int targetCurrencyId = resultSet.getInt("TargetCurrencyId");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, RoundingMode.HALF_UP);

        Currency baseCurrency = currencyDao.findById(baseCurrencyId).orElse(null);
        Currency targetCurrency = currencyDao.findById(targetCurrencyId).orElse(null);
        return new ExchangeCurrency(id, baseCurrency, targetCurrency,rate);
    }
}
