package org.project.cursexchange.dao;

import org.project.cursexchange.dto.ExchangeCurrencyDTO;
import org.project.cursexchange.mapper.ExchangeCurrencyMapper;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoImpl extends BaseDao<ExchangeRate> implements ExchangeCurrencyDao {
    private final String nameExchangeCurrencyTable = "ExchangeRates";
    private final Dao dao;
    private final ExchangeCurrencyMapper exchangeCurrencyMapper;

    public ExchangeRateDaoImpl(Dao dao,
                               ExchangeCurrencyMapper exchangeCurrencyMapper) {
        this.dao = dao;
        this.exchangeCurrencyMapper = exchangeCurrencyMapper;
    }

    @Override
    public List<ExchangeRate> findAllCurrencyExchange() throws SQLException {
        return findAll(nameExchangeCurrencyTable, exchangeCurrencyMapper);
    }

    @Override
    public Optional<ExchangeRate> findById(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        String[] fields = {"BaseCurrencyId", "TargetCurrencyId"};
        Object[] values = {baseCurrency.getId(), targetCurrency.getId()};
        return findByFields(fields, values, nameExchangeCurrencyTable, exchangeCurrencyMapper);
    }

    @Override
    public boolean save(ExchangeCurrencyDTO exchangeCurrencyDto) throws SQLException {
        String[] fieldsToSave = {"BaseCurrencyId", "TargetCurrencyId", "Rate"};
        Object[] valuesToSave = new Object[]{exchangeCurrencyDto.getCurrencyBase().getId(), exchangeCurrencyDto.getCurrencyTarget().getId(), exchangeCurrencyDto.getRate()};
        return save(nameExchangeCurrencyTable, fieldsToSave, valuesToSave);
    }

    @Override
    public boolean update(ExchangeRate exchangeRate, String value) throws SQLException {
        boolean isUpdate = updateField("rate", value, exchangeRate.getId(), nameExchangeCurrencyTable);
        if (isUpdate) {
            return true;
        }
        return false;
    }

    @Override
    public List<ExchangeRate> findCurrencyExchangeByTargetCode(String code) throws SQLException {
        String joinCondition = nameExchangeCurrencyTable + ".TargetCurrencyId = Currencies.id";
        return findByFieldWithJoin(nameExchangeCurrencyTable, "code", code, "Currencies", joinCondition, exchangeCurrencyMapper);
    }


}
