package org.project.cursexchange.mapper;

import org.project.cursexchange.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper {
    public static Currency mapRowToCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Sign"));
    }
}
