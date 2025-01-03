package org.project.cursexchange.dao;

import org.project.cursexchange.Database;
import org.project.cursexchange.models.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Currency> {
    private final String nameCurrencyTable = "Currencies";
    @Override
    public Optional<Currency> findById(int id) throws SQLException {
        return Optional.empty();
    }

    public List<Currency> convertSetToObjects(ResultSet resultSet) throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        while (resultSet.next()) {
            int id=resultSet.getInt("id");
            String code=resultSet.getString("Code");
            String name=resultSet.getString("FullName");
            String sign=resultSet.getString("Sign");
            Currency currency=new Currency((long) id,code,name,sign);
            currencies.add(currency);
        }
        return  currencies;
    }

    @Override
    public Optional<Currency> findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM "+ nameCurrencyTable + " WHERE code = ?";
        Connection connection=Database.getConnection();
        PreparedStatement stmt=connection.prepareStatement(sql);
        stmt.setString(1, code);
        ResultSet resultSet=stmt.executeQuery();
        List<Currency> listOfCurrencies = convertSetToObjects(resultSet);
        if(!listOfCurrencies.isEmpty()){
            return  Optional.ofNullable(listOfCurrencies.get(listOfCurrencies.size()-1));
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() throws SQLException {

        List<Currency> currencies = new ArrayList<>();
        String sql = "select * from "+ nameCurrencyTable;
        Connection connection=Database.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertSetToObjects(resultSet);
    }

    @Override
    public Optional<Currency> add(Currency currency) throws SQLException {
        Connection connection= Database.getConnection();
        String sqlRequest = "INSERT INTO " + nameCurrencyTable + " (Code, FullName, Sign) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);
        preparedStatement.setString(1, currency.getCode());
        preparedStatement.setString(2, currency.getName());
        preparedStatement.setString(3, currency.getSign());
        Optional<Currency> currencyByCodeExisting=findByCode(currency.getCode());
        if(currencyByCodeExisting.isEmpty()){
            preparedStatement.executeUpdate();
            currencyByCodeExisting=findByCode(currency.getCode());
            return currencyByCodeExisting;
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public void update(Currency currency) {

    }
}
