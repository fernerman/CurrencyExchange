package org.project.cursexchange.dao;

import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.mapper.ExchangeRateMapper;
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
            SELECT er.id,
            er.Rate,
            er.BaseCurrencyId,
            er.TargetCurrencyId,
            bc.id AS BaseCurrencyId,
            bc.code AS BaseCurrencyCode,
            bc.name AS BaseCurrencyName,
            bc.sign AS BaseCurrencySign,
            tc.id AS TargetCurrencyId,
            tc.code AS TargetCurrencyCode,
            tc.name AS TargetCurrencyName,
            tc.sign AS TargetCurrencySign
            FROM ExchangeRate er
            JOIN Currency bc ON er.BaseCurrencyId = bc.id
            JOIN Currency tc ON er.TargetCurrencyId = tc.id
            """;

    private final String SQL_FIND_BY_ID = """
            SELECT er.id, er.Rate,er.BaseCurrencyId, er.TargetCurrencyId,
            bc.id AS BaseCurrencyId, bc.code AS BaseCurrencyCode, bc.name AS BaseCurrencyName, bc.sign AS BaseCurrencySign,
            tc.id AS TargetCurrencyId, tc.code AS TargetCurrencyCode, tc.name AS TargetCurrencyName, tc.sign AS TargetCurrencySign
            FROM ExchangeRate er
            JOIN Currency bc ON er.BaseCurrencyId = bc.id
            JOIN Currency tc ON er.TargetCurrencyId = tc.id
            WHERE er.id = ?
            """;

    private final String SQL_FIND_BY_CODE = """
             SELECT er.id, er.Rate,er.BaseCurrencyId, er.TargetCurrencyId, 
             bc.id AS BaseCurrencyId, bc.code AS BaseCurrencyCode, bc.name AS BaseCurrencyName, bc.sign AS BaseCurrencySign,
             tc.id AS TargetCurrencyId, tc.code AS TargetCurrencyCode, tc.name AS TargetCurrencyName, tc.sign AS TargetCurrencySign
             FROM ExchangeRate er
             JOIN Currency bc ON er.BaseCurrencyId = bc.id
             JOIN Currency tc ON er.TargetCurrencyId = tc.id
             WHERE bc.code = ? OR tc.code = ?";
            """;
    private final String SQL_SAVE = """
            WITH inserted AS (
            INSERT INTO ExchangeRate (BaseCurrencyId, TargetCurrencyId, Rate)
            SELECT 
            bc.id AS BaseCurrencyId,
            tc.id AS TargetCurrencyId,
            ? AS Rate
            FROM Currency bc
            JOIN Currency tc ON tc.code = ?
            WHERE bc.code = ?
            RETURNING id, BaseCurrencyId, TargetCurrencyId, Rate)
                SELECT 
                inserted.id AS ExchangeRateId,
                inserted.Rate AS ExchangeRateRate,
                bc.id AS BaseCurrencyId,
                bc.code AS BaseCurrencyCode,
                bc.name AS BaseCurrencyName,
                bc.sign AS BaseCurrencySign,
                tc.id AS TargetCurrencyId,
                tc.code AS TargetCurrencyCode,
                tc.name AS TargetCurrencyName,
                tc.sign AS TargetCurrencySign
            FROM inserted
            JOIN Currency bc ON inserted.BaseCurrencyId = bc.id
            JOIN Currency tc ON inserted.TargetCurrencyId = tc.id
            """;

    private final String SQL_UPDATE = """
            WITH updated AS (
                UPDATE ExchangeRate
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
            JOIN Currency bc ON updated.BaseCurrencyId = bc.id
            JOIN Currency tc ON updated.TargetCurrencyId = tc.id
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
                JOIN Currency bc
                    ON er1.BaseCurrencyId = bc.id
                JOIN Currency tc
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
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODE)) {
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
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE)) {
            statement.setBigDecimal(1, exchangeRateDTO.getRate());
            statement.setString(2, exchangeRateDTO.getBaseCurrencyCode());
            statement.setString(3, exchangeRateDTO.getTargetCurrencyCode());
            ResultSet resultSet = statement.executeQuery();
            return ExchangeRateMapper.mapRowToExchangeRate(resultSet);
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
