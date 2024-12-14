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
    private final String nameTable = "Currencies";
    @Override
    public Optional<Currency> findById(int id) {

        return Optional.empty();
    }


    @Override
    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "select * from "+nameTable;
        Connection connection=Database.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
                int id=resultSet.getInt("id");
                String code=resultSet.getString("Code");
                String name=resultSet.getString("FullName");
                String sign=resultSet.getString("Sign");
                Currency currency=new Currency(id,code,name,sign);
                currencies.add(currency);
        }
        return currencies;
    }

    @Override
    public void save(Currency currency) {

    }

    @Override
    public void update(Currency currency) {

    }
}
