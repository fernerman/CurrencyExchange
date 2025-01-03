package org.project.cursexchange;

import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dao.ExchangeCurrencyDaoImpl;
import org.project.cursexchange.mappers.ExchangeCurrencyMapper;

public class DependencyFactory {
    public static CurrencyDao createCurrencyDao() {
        return new CurrencyDaoImpl();
    }

    public static ExchangeCurrencyMapper createExchangeCurrencyMapper(CurrencyDao currencyDao) {
        return new ExchangeCurrencyMapper(currencyDao);
    }

    public static ExchangeCurrencyDaoImpl createExchangeCurrencyDao() {
        CurrencyDao currencyDao = createCurrencyDao();
        ExchangeCurrencyMapper mapper = createExchangeCurrencyMapper(currencyDao);
        return new ExchangeCurrencyDaoImpl(currencyDao, mapper);
    }
}