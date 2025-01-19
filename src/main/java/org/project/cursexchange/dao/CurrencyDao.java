package org.project.cursexchange.dao;

import org.project.cursexchange.dto.SaveCurrencyDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccessException;
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
    private static final String SQL_FIND_BY_ID = "SELECT * FROM Currencies WHERE id=?";
    private static final String SQL_FIND_BY_CODE = "SELECT * FROM Currencies WHERE Code=?";
    private static final String SQL_SAVE = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

    public Optional<Currency> findById(int id) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return Optional.empty();
    }

    public Optional<Currency> findByCode(String code) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODE)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of((mapRowToCurrency(resultSet)));
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        throw new CurrencyNotFound();
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Statement statement = DatabaseConnection.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                currencies.add(mapRowToCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return currencies;
    }

    public Currency save(SaveCurrencyDTO currency) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE, new String[]{"id"})) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            // Получение сгенерированного идентификатора
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong("id");
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
                throw new CurrencyExistException();
            } else {
                throw new DataAccessException();
            }
        }
    }

    private Currency mapRowToCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"), resultSet.getString("Code"), resultSet.getString("FullName"), resultSet.getString("Sign"));
    }
}
