package org.project.cursexchange.dao;

import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.mapper.ExchangeRateMapper;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {
    private final static String SQL_FIND_ALL = """
            SELECT *
            FROM ExchangeRates er
            JOIN Currencies bc ON er.BaseCurrencyId = bc.id
            JOIN Currencies tc ON er.TargetCurrencyId = tc.id
            """;

    private final String SQL_FIND_BY_ID = """
            SELECT
                er.id,
                er.Rate,
                er.BaseCurrencyId AS ExchangeRateBaseCurrencyId,
                er.TargetCurrencyId AS ExchangeRateTargetCurrencyId,
                bc.id AS BaseCurrencyId,
                bc.code AS BaseCurrencyCode,
                bc.FullName AS BaseCurrencyName,
                bc.Sign AS BaseCurrencySign,
                tc.id AS TargetCurrencyId,
                tc.code AS TargetCurrencyCode,
                tc.FullName AS TargetCurrencyName,
                tc.Sign AS TargetCurrencySign
            FROM ExchangeRates er
            JOIN Currencies bc ON er.BaseCurrencyId = bc.id
            JOIN Currencies tc ON er.TargetCurrencyId = tc.id
            WHERE er.id = ?
            """;

    private final String SQL_FIND_BY_CODES = """
             SELECT er.id, er.Rate,er.BaseCurrencyId, er.TargetCurrencyId,
             bc.id AS BaseCurrencyId, bc.Code AS BaseCurrencyCode, bc.FullName AS BaseCurrencyName, bc.Sign AS BaseCurrencySign,
             tc.id AS TargetCurrencyId, tc.Code AS TargetCurrencyCode, tc.FullName AS TargetCurrencyName, tc.Sign AS TargetCurrencySign;
             FROM ExchangeRates er
             JOIN Currencies bc ON er.BaseCurrencyId = bc.id
             JOIN Currencies tc ON er.TargetCurrencyId = tc.id
             WHERE bc.code = ? AND tc.code = ?
            """;
    private final String SQL_SAVE = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private final String SQL_UPDATE = """
            WITH updated AS (
                UPDATE ExchangeRates
                SET Rate = ?
                WHERE BaseCurrencyId = (SELECT id FROM Currency WHERE code = ?)
                  AND TargetCurrencyId = (SELECT id FROM Currency WHERE code = ?)
                RETURNING id, BaseCurrencyId, TargetCurrencyId, Rate
            )
            SELECT 
                updated.id AS ExchangeRateId,
                updated.Rate AS ExchangeRateRate,
                bc.id AS BaseCurrencyId,
                bc.code AS BaseCurrencyCode,
                bc.name AS BaseCurrencyName,
                bc.sign AS BaseCurrencySign,
                tc.id AS TargetCurrencyId,
                tc.code AS TargetCurrencyCode,
                tc.name AS TargetCurrencyName,
                tc.sign AS TargetCurrencySign
            FROM updated
            JOIN Currencies bc ON updated.BaseCurrencyId = bc.id
            JOIN Currencies tc ON updated.TargetCurrencyId = tc.id
            """;

    private final String SQL_GET_RATE_BY_INTERMEDIATE_CURRENCY = """
            SELECT
                er1.id AS id,
                (er2.Rate / er1.Rate) AS Rate,
                bc.id AS BaseCurrencyId,
                bc.code AS BaseCurrencyCode,
                bc.name AS BaseCurrencyName,
                bc.sign AS BaseCurrencySign,
                tc.id AS TargetCurrencyId,
                tc.code AS TargetCurrencyCode,
                tc.name AS TargetCurrencyName,
                tc.sign AS TargetCurrencySign
                FROM ExchangeRate er1
                JOIN ExchangeRate er2
                 ON er1.BaseCurrencyId = er2.BaseCurrencyId
                JOIN Currencies bc
                    ON er1.BaseCurrencyId = bc.id
                JOIN Currencies tc
                    ON er2.TargetCurrencyId = tc.id
                WHERE er1.TargetCurrencyId = (SELECT id FROM Currency WHERE code = ?)
                  AND er2.TargetCurrencyId = (SELECT id FROM Currency WHERE code = ?)
                  AND er1.BaseCurrencyId = (SELECT id FROM Currency WHERE code = ?);
            """;

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Statement statement = DatabaseConnection.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                exchangeRates.add(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
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
                    return Optional.of(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding exchange rate by ID", e);
        }
        return Optional.empty();
    }

    public Optional<ExchangeRate> findByCodes(String base, String target) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODES)) {
            statement.setString(1, base);
            statement.setString(2, target);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding exchange rate by code", e);
        }
        return Optional.empty();
    }

    public Optional<ExchangeRate> findRateByIntermediateCurrency(String base, String target, String intermediateCurrency) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_GET_RATE_BY_INTERMEDIATE_CURRENCY)) {
            statement.setString(1, base);
            statement.setString(2, target);
            statement.setString(3, intermediateCurrency);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return Optional.empty();
    }

    public ExchangeRate save(SaveExchangeRateDTO exchangeRateDTO) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(SQL_SAVE)) {
                statement.setBigDecimal(1, exchangeRateDTO.getRate());
                statement.setString(2, exchangeRateDTO.getBaseCurrencyCode());
                statement.setString(3, exchangeRateDTO.getTargetCurrencyCode());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Optional<ExchangeRate> savedExchangeRate = findById(resultSet.getInt("id"));
                    if (savedExchangeRate.isPresent()) {
                        return savedExchangeRate.get();
                    }

                }
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        throw new DataAccessException();
    }

    public ExchangeRate update(SaveExchangeRateDTO saveExchangeRateDTO) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_UPDATE)) {
            statement.setBigDecimal(1, saveExchangeRateDTO.getRate());
            statement.setString(2, saveExchangeRateDTO.getBaseCurrencyCode());
            statement.setString(3, saveExchangeRateDTO.getTargetCurrencyCode());
            ResultSet resultSet = statement.executeQuery();
            return ExchangeRateMapper.mapRowToExchangeRate(resultSet);
        } catch (SQLException ex) {
            throw new DataAccessException();
        }
    }
}
