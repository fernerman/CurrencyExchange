package org.project.cursexchange.dao;

import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccesException;
import org.project.cursexchange.mapper.CurrencyRowMapper;
import org.project.cursexchange.model.Currency;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoImpl extends BaseDao<Currency> implements CurrencyDao {

    private final String nameCurrencyTable = "Currencies";
    private final CurrencyRowMapper currencyRowMapper = new CurrencyRowMapper();

    @Override
    public Optional<Currency> findById(int id) {
        try {
            return findByField("id", id, nameCurrencyTable, currencyRowMapper);
        }
        catch (SQLException e) {
            throw new DataAccesException();
        }
    }

    @Override
    public Currency findByCode(String code) {
        try {
            Optional<Currency> currency = findByField("Code", code, nameCurrencyTable, currencyRowMapper);
            if (currency.isEmpty()) {
                throw new CurrencyNotFound();
            }
            return currency.get();
        }
        catch (SQLException ex){
            throw new DataAccesException();
        }
    }

    @Override
    public List<Currency> findAll(){
        try {
            return findAll(nameCurrencyTable, currencyRowMapper);
        }
        catch (SQLException ex){
            throw new DataAccesException();
        }
    }

    @Override
    public boolean saveCurrency(CurrencyDTO currencyDto)  {
        try {
            String[] columns=new String[]{"Code", "FullName", "Sign"};
            Currency currency= currencyDto.toEntity();
            String[] values=new String[]{currency.getCode(),currency.getName(),currency.getSign()};
            return save(nameCurrencyTable,columns,values);
        }
        catch (SQLException ex){
            if(ex.getMessage().contains("[SQLITE_CONSTRAINT_UNIQUE]")){
                throw new CurrencyExistException();
            }
            else {
                throw new DataAccesException();
            }
        }
    }
}
