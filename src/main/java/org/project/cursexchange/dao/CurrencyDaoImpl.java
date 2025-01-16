package org.project.cursexchange.dao;

import org.project.cursexchange.config.DatabaseConnection;
import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccesException;
import org.project.cursexchange.mapper.CurrencyRowMapper;
import org.project.cursexchange.model.Currency;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoImpl implements Dao<Currency> {

    private final String NAME_TABLE_CURRENCY = "Currencies";
    private final String SQL_FIND_ALL = "SELECT * FROM " + NAME_TABLE_CURRENCY;
    private final String SQL_FIND_BY_ID = "SELECT * FROM " + NAME_TABLE_CURRENCY + " WHERE id=?";
    private final String SQL_FIND_BY_CODE = "SELECT * FROM " + NAME_TABLE_CURRENCY + " WHERE code=?";
    private static final String SQL_SAVE = "INSERT INTO currency (code, name, sign) VALUES (?, ?, ?)";


    @Override
    public Optional<Currency> findById(int id) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccesException();
        }
        return Optional.empty();
    }

    @Override
    public Currency findByCode(String code) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODE)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return (mapRowToCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccesException();
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
            throw new DataAccesException();
        }
        return currencies;
    }

    @Override
    public void save(Currency currency) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyExistException();
            } else {
                throw new DataAccesException();
            }
        }
    }


    private Currency mapRowToCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Sign")
        );
    }
}
