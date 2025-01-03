package org.project.cursexchange.mappers;

import org.project.cursexchange.models.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyRowMapper implements RowMapper<Currency> {

    @Override
    public Currency mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String code = resultSet.getString("Code");
        String name = resultSet.getString("FullName");
        String sign = resultSet.getString("Sign");
        return new Currency(id, code, name, sign);
    }
}