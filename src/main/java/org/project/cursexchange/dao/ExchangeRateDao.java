package org.project.cursexchange.dao;

import org.project.cursexchange.util.DatabaseConnection;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
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
    private final String SQL_SAVE =
            """
                    INSERT INTO ExchangeRate (BaseCurrencyId, TargetCurrencyId, Rate)
                    VALUES (?, ?, ?)
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


    public Optional<ExchangeRate> findByCode(String code) {
        return findByCodes(code, code);
    }


    public ExchangeRate save(ExchangeRate exchangeRate) {

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_SAVE)) {
            statement.setLong(1, exchangeRate.getBaseCurrency().getId());
            statement.setLong(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());

            int rowsInserted = statement.executeUpdate();
            // Получение сгенерированного идентификатора
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exchangeRate.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Failed to retrieve the ID.");
                }
            }
            if (rowsInserted == 0) {
                throw new SQLException("Failed to insert exchange rate, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exchange rate", e);
        }
        return exchangeRate;
    }

    private ExchangeRate mapRowToExchangeRate(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        BigDecimal rate = resultSet.getBigDecimal("Rate").setScale(2, BigDecimal.ROUND_HALF_UP);

        Currency baseCurrency = new Currency(
                resultSet.getInt("BaseCurrencyId"),
                resultSet.getString("BaseCurrencyCode"),
                resultSet.getString("BaseCurrencyName"),
                resultSet.getString("BaseCurrencySign")
        );

        Currency targetCurrency = new Currency(
                resultSet.getInt("TargetCurrencyId"),
                resultSet.getString("TargetCurrencyCode"),
                resultSet.getString("TargetCurrencyName"),
                resultSet.getString("TargetCurrencySign")
        );

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }


//    @Override
//    public boolean save(ExchangeCurrencyDTO exchangeCurrencyDto) throws SQLException {
//        String[] fieldsToSave = {"BaseCurrencyId", "TargetCurrencyId", "Rate"};
//        Object[] valuesToSave = new Object[]{exchangeCurrencyDto.getCurrencyBase().getId(), exchangeCurrencyDto.getCurrencyTarget().getId(), exchangeCurrencyDto.getRate()};
//        return save(NAME_TABLE_EXCHANGE_RATE, fieldsToSave, valuesToSave);
//    }
//
//    @Override
//    public boolean update(ExchangeRate exchangeRate, String value) throws SQLException {
//        boolean isUpdate = updateField("rate", value, exchangeRate.getId(), NAME_TABLE_EXCHANGE_RATE);
//        if (isUpdate) {
//            return true;
//        }
//        return false;
//    }
//

//    public List<ExchangeRate> findCurrencyExchangeByTargetCode(String code) throws SQLException {
//        String joinCondition = NAME_TABLE_EXCHANGE_RATE + ".TargetCurrencyId = Currencies.id";
//        return findByFieldWithJoin(NAME_TABLE_EXCHANGE_RATE, "code", code, "Currencies", joinCondition, exchangeCurrencyMapper);
//    }


}
