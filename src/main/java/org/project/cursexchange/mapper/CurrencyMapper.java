package org.project.cursexchange.mapper;

import org.project.cursexchange.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper {
    public static final String ID = "Id";
    public static final String CURRENCY_CODE = "Code";
    public static final String FULL_NAME = "FullName";
    public static final String SIGN = "Sign";

    public static Currency mapRowToCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt(ID),
                resultSet.getString(CURRENCY_CODE),
                resultSet.getString(FULL_NAME),
                resultSet.getString(SIGN));
    }
}
