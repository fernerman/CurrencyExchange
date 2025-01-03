package org.project.cursexchange.dao;

import org.project.cursexchange.dto.ExchangeCurrencyDTO;
import org.project.cursexchange.exceptions.CurrencyExchangeNotFound;
import org.project.cursexchange.mappers.ExchangeCurrencyMapper;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeCurrencyDaoImpl extends BaseDao<ExchangeCurrency> implements ExchangeCurrencyDao {
    private final String nameExchangeCurrencyTable = "ExchangeRates";
    private  final CurrencyDao currencyDao;
    private  final ExchangeCurrencyMapper exchangeCurrencyMapper;

    public ExchangeCurrencyDaoImpl(CurrencyDao currencyDao,
                                   ExchangeCurrencyMapper exchangeCurrencyMapper){
        this.currencyDao = currencyDao;
        this.exchangeCurrencyMapper = exchangeCurrencyMapper;
    }
    @Override
    public List<ExchangeCurrency> findAllCurrencyExchange() throws SQLException {
        return findAll(nameExchangeCurrencyTable,exchangeCurrencyMapper);
    }
    @Override
    public Optional<ExchangeCurrency> findCurrencyExchangeById(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        String[] fields = {"BaseCurrencyId", "TargetCurrencyId"};
        Object[] values = {baseCurrency.getId(), targetCurrency.getId()};
        return findByFields(fields,values,nameExchangeCurrencyTable,exchangeCurrencyMapper);
    }

    @Override
    public boolean saveCurrencyExchange(ExchangeCurrencyDTO exchangeCurrencyDto) throws SQLException {
        String[] fieldsToSave = {"BaseCurrencyId", "TargetCurrencyId","Rate"};
        Object[] valuesToSave = new Object[]{exchangeCurrencyDto.getCurrencyBase().getId(), exchangeCurrencyDto.getCurrencyTarget().getId(), exchangeCurrencyDto.getRate()};
        return  save(nameExchangeCurrencyTable, fieldsToSave,valuesToSave);
    }

    @Override
    public boolean updateRateCurrencyExchange(ExchangeCurrency exchangeCurrency, String value) throws SQLException {
        boolean isUpdate= updateField("rate",value,exchangeCurrency.getId(),nameExchangeCurrencyTable);
        if(isUpdate){
            return true;
        }
        return false;
    }

    @Override
    public List<ExchangeCurrency> findCurrencyExchangeByTargetCode(String code) throws SQLException {
        String joinCondition=nameExchangeCurrencyTable+".TargetCurrencyId = Currencies.id";
        return findByFieldWithJoin(nameExchangeCurrencyTable,"code",code,"Currencies",joinCondition,exchangeCurrencyMapper);
    }


}
