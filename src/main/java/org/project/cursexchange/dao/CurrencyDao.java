package org.project.cursexchange.dao;

import org.project.cursexchange.dto.RequestCurrencyDTO;
import org.project.cursexchange.exception.CurrencyAlreadyExistException;
import org.project.cursexchange.mapper.CurrencyMapper;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.util.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {
    private static final String SQL_FIND_ALL = "SELECT * FROM Currencies";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM Currencies WHERE ID=?";
    private static final String SQL_FIND_BY_CODE = "SELECT * FROM Currencies WHERE Code=?";
    private static final String SQL_SAVE = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
    public Optional<Currency> findById(int id) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(CurrencyMapper.mapRowToCurrency(resultSet));
            }
        }
        return Optional.empty();
    }
    public Optional<Currency> findByCode(String code) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODE)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of((CurrencyMapper.mapRowToCurrency(resultSet)));
            }
        }
        return Optional.empty();
    }
    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        try (Statement statement = DatabaseConnection.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                currencies.add(CurrencyMapper.mapRowToCurrency(resultSet));
            }
        }
        return currencies;
    }
    public Currency save(RequestCurrencyDTO currency) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new Currency(id,
                            currency.getCode(),
                            currency.getName(),
                            currency.getSign());
                } else {
                    throw new SQLException("Failed to retrieve the ID.");
                }
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyAlreadyExistException();
            } else {
                throw new SQLException();
            }
        }
    }
}
