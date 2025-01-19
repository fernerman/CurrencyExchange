package org.project.cursexchange.dao;

import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.util.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {
    private final static String SQL_FIND_ALL = """
            SELECT er.id, er.BaseCurrencyId, er.TargetCurrencyId, er.Rate,
            bc.id AS BaseCurrencyId, bc.code AS BaseCurrencyCode, bc.name AS BaseCurrencyName, bc.sign AS BaseCurrencySign,
            tc.id AS TargetCurrencyId, tc.code AS TargetCurrencyCode, tc.name AS TargetCurrencyName, tc.sign AS TargetCurrencySign
            FROM ExchangeRate er
            JOIN Currency bc ON er.BaseCurrencyId = bc.id
            JOIN Currency tc ON er.TargetCurrencyId = tc.id
            """;

    private final String SQL_FIND_BY_ID = """
            SELECT er.id, er.BaseCurrencyId, er.TargetCurrencyId, er.Rate,
            bc.id AS BaseCurrencyId, bc.code AS BaseCurrencyCode, bc.name AS BaseCurrencyName, bc.sign AS BaseCurrencySign,
            tc.id AS TargetCurrencyId, tc.code AS TargetCurrencyCode, tc.name AS TargetCurrencyName, tc.sign AS TargetCurrencySign
            FROM ExchangeRate er
            JOIN Currency bc ON er.BaseCurrencyId = bc.id
            JOIN Currency tc ON er.TargetCurrencyId = tc.id
            WHERE er.id = ?
            """;

    private final String SQL_FIND_BY_CODE = """
             SELECT er.id, er.BaseCurrencyId, er.TargetCurrencyId, er.Rate,
             bc.id AS BaseCurrencyId, bc.code AS BaseCurrencyCode, bc.name AS BaseCurrencyName, bc.sign AS BaseCurrencySign,
             tc.id AS TargetCurrencyId, tc.code AS TargetCurrencyCode, tc.name AS TargetCurrencyName, tc.sign AS TargetCurrencySign
             FROM ExchangeRate er
             JOIN Currency bc ON er.BaseCurrencyId = bc.id
             JOIN Currency tc ON er.TargetCurrencyId = tc.id
             WHERE bc.code = ? OR tc.code = ?";
            """;
    private final String SQL_SAVE = """
                INSERT INTO ExchangeRate (BaseCurrencyId, TargetCurrencyId, Rate)
                VALUES (
                    (SELECT id FROM Currency WHERE code = ?),
                    (SELECT id FROM Currency WHERE code = ?),
                    ?
                )
            """;

    private final String SQL_UPDATE = """
            UPDATE ExchangeRate
            SET Rate = ?
            WHERE BaseCurrencyId = (SELECT id FROM Currency WHERE code = ?)
            AND TargetCurrencyId = (SELECT id FROM Currency WHERE code = ?)
            """;

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Statement statement = DatabaseConnection.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                exchangeRates.add(mapRowToExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return exchangeRates;
    }

    public Optional<ExchangeRate> findById(int id) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException("Error finding exchange rate by ID", e);
        }

        return Optional.empty();
    }

    public Optional<ExchangeRate> findByCodes(String base, String target) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODE)) {
            statement.setString(1, base);
            statement.setString(2, target);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding exchange rate by code", e);
        }

        return Optional.empty();
    }

    public ExchangeRate save(SaveExchangeRateDTO exchangeRateDTO) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE)) {
            statement.setString(1, exchangeRateDTO.getBaseCurrencyCode());
            statement.setString(2, exchangeRateDTO.getBaseCurrencyCode());
            statement.setBigDecimal(3, exchangeRateDTO.getRate());
            statement.executeUpdate();
            return getExchangeRateByGenerateId(statement);
        } catch (SQLException ex) {
            if (ex.getMessage().contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyExistException();
            } else {
                throw new DataAccessException();
            }
        }
    }

    public ExchangeRate update(SaveExchangeRateDTO saveExchangeRateDTO) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_UPDATE)) {
            statement.setString(1, saveExchangeRateDTO.getBaseCurrencyCode());
            statement.setString(2, saveExchangeRateDTO.getTargetCurrencyCode());
            statement.setBigDecimal(3, saveExchangeRateDTO.getRate());
            return getExchangeRateByGenerateId(statement);
        } catch (SQLException ex) {
            throw new DataAccessException();
        }
    }

    private ExchangeRate getExchangeRateByGenerateId(PreparedStatement statement) throws SQLException {
        // Получение сгенерированного идентификатора
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong(1);
                Optional<ExchangeRate> optionalExchangeRate = findById((int) id);
                if (optionalExchangeRate.isPresent()) {
                    return optionalExchangeRate.get();
                }
            }
        }
        throw new SQLException("Failed to retrieve the ID.");
    }

    private ExchangeRate mapRowToExchangeRate(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, RoundingMode.HALF_UP);

        Currency baseCurrency = new Currency(resultSet.getInt("BaseCurrencyId"), resultSet.getString("BaseCurrencyCode"), resultSet.getString("BaseCurrencyName"), resultSet.getString("BaseCurrencySign"));

        Currency targetCurrency = new Currency(resultSet.getInt("TargetCurrencyId"), resultSet.getString("TargetCurrencyCode"), resultSet.getString("TargetCurrencyName"), resultSet.getString("TargetCurrencySign"));

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }

//    public List<ExchangeRate> findCurrencyExchangeByTargetCode(String code) throws SQLException {
//        String joinCondition = NAME_TABLE_EXCHANGE_RATE + ".TargetCurrencyId = Currencies.id";
//        return findByFieldWithJoin(NAME_TABLE_EXCHANGE_RATE, "code", code, "Currencies", joinCondition, exchangeCurrencyMapper);
//    }


}
