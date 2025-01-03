package org.project.cursexchange.dao;

import org.project.cursexchange.Database;
import org.project.cursexchange.models.ExchangeCurrency;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeCurrencyDao implements Dao<ExchangeCurrency> {
    private final String nameExchangeCurrencyTable = "ExchangeRates";
    @Override
    public Optional<ExchangeCurrency> findById(int id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeCurrency> findByCode(String code) throws SQLException {
        return Optional.empty();
    }
    public List<ExchangeCurrency> convertSetToObjects(ResultSet resultSet) throws SQLException {
        List<ExchangeCurrency> exchangeCurrencies = new ArrayList<>();
        while (resultSet.next()) {
            int id=resultSet.getInt("id");
            int baseCurrencyId=resultSet.getInt("baseCurrencyId");
            int targetCurrencyId=resultSet.getInt("targetCurrencyId");
            BigDecimal rate=resultSet.getBigDecimal("Rate");
            ExchangeCurrency exchangeCurrency=new ExchangeCurrency(id,baseCurrencyId,targetCurrencyId,rate);
            exchangeCurrencies.add(exchangeCurrency);
        }
        return exchangeCurrencies;
    }
    @Override
    public List<ExchangeCurrency> findAll() throws SQLException {
        String sql = "select * from "+ nameExchangeCurrencyTable;
        Connection connection= Database.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertSetToObjects(resultSet);
    }

    @Override
    public Optional<ExchangeCurrency> add(ExchangeCurrency exchangeCurrency) throws SQLException {
        return Optional.empty();
    }

    @Override
    public void update(ExchangeCurrency exchangeCurrency) {

    }
}
