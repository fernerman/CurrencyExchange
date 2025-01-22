package org.project.cursexchange.dao;

import org.project.cursexchange.dto.ExchangeRequestDto;
import org.project.cursexchange.dto.RequestExchangeRateDTO;
import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.dto.ResponseExchangeRateDTO;
import org.project.cursexchange.mapper.ExchangeRateMapper;
import org.project.cursexchange.mapper.ExchangeRateWithAmountMapper;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.util.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExchangeRateDao {
    private final static String SQL_FIND_ALL = """
            SELECT *
            FROM ExchangeRates er
            JOIN Currencies bc ON er.BaseCurrencyId = bc.ID
            JOIN Currencies tc ON er.TargetCurrencyId = tc.ID
            """;
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final String SQL_FIND_BY_CODES = """
             SELECT *
             FROM ExchangeRates er
             JOIN Currencies bc ON er.BaseCurrencyId = bc.ID
             JOIN Currencies tc ON er.TargetCurrencyId = tc.ID
             WHERE bc.code = ? AND tc.code = ?
            """;
    private final String SQL_SAVE = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
            VALUES (?, ?, ?)
            """;

    private final String SQL_UPDATE = """
            UPDATE ExchangeRates
            SET Rate = ?
            WHERE BaseCurrencyId = (SELECT ID FROM Currencies WHERE code = ?)
              AND TargetCurrencyId = (SELECT ID FROM Currencies WHERE code = ?);
            """;

    private final String SQL_GET_RATE_BY_INTERMEDIATE_CURRENCY = """
            SELECT
                (er2.Rate / er1.Rate)
                FROM ExchangeRates er1
                JOIN ExchangeRates er2
                 ON er1.BaseCurrencyId = er2.BaseCurrencyId
                WHERE er1.TargetCurrencyId = (SELECT ID FROM Currencies WHERE code = ?)
                  AND er2.TargetCurrencyId = (SELECT ID FROM Currencies WHERE code = ?)
                  AND er1.BaseCurrencyId = (SELECT ID FROM Currencies WHERE code = ?);
            """;

    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Statement statement = DatabaseConnection.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                exchangeRates.add(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
            }
        }
        return exchangeRates;
    }
    public Optional<ExchangeRate> findByCodes(String base, String target) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_FIND_BY_CODES)) {
            statement.setString(1, base);
            statement.setString(2, target);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(ExchangeRateMapper.mapRowToExchangeRate(resultSet));
                }
            }
        }
        return Optional.empty();
    }
    public Optional<ResponseExchangeDTO> findDirectExchangeRate(ExchangeRequestDto dto) throws SQLException {
        Optional<ExchangeRate> exchangeRateDirect = this.findByCodes(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
        if (exchangeRateDirect.isPresent()) {
            var responseExchangeRateDTO = new ResponseExchangeRateDTO(exchangeRateDirect.get().getBaseCurrency(), exchangeRateDirect.get().getTargetCurrency(), exchangeRateDirect.get().getRate());
            return Optional.of(ExchangeRateWithAmountMapper.toDTO(responseExchangeRateDTO, dto.getAmount()));
        }
        return Optional.empty();
    }
    public Optional<ResponseExchangeDTO> findReversedExchangeRate(ExchangeRequestDto dto) throws SQLException {
        Optional<ExchangeRate> reversedRate = this.findByCodes(dto.getTargetCurrencyCode(), dto.getBaseCurrencyCode());
        if (reversedRate.isPresent()) {
            BigDecimal rate = BigDecimal.ONE.divide(reversedRate.get().getRate(), 6, RoundingMode.HALF_UP);
            var responseExchangeRateDTO = new ResponseExchangeRateDTO(reversedRate.get().getBaseCurrency(), reversedRate.get().getTargetCurrency(), rate);
            return Optional.of(ExchangeRateWithAmountMapper.toDTO(responseExchangeRateDTO, dto.getAmount()));
        }
        return Optional.empty();
    }
    public Optional<ResponseExchangeDTO> findIntermediateExchangeRate(ExchangeRequestDto dto, String intermediateCurrencyCode) throws SQLException {
        BigDecimal rate = findRateByIntermediateCurrency(dto, intermediateCurrencyCode);
        if (!Objects.equals(rate, BigDecimal.ZERO)) {
            RequestExchangeRateDTO requestExchangeRateDTO = new RequestExchangeRateDTO(dto.getBaseCurrencyCode(), intermediateCurrencyCode, rate);
            Optional<ResponseExchangeRateDTO> responseExchangeRate = getResponseExchangeRate(requestExchangeRateDTO);
            if (responseExchangeRate.isPresent()) {
                return Optional.of(ExchangeRateWithAmountMapper.toDTO(responseExchangeRate.get(), dto.getAmount()));
            }
        }
        return Optional.empty();
    }
    public BigDecimal findRateByIntermediateCurrency(ExchangeRequestDto exchangeRequestDto, String intermediateCurrencyCode) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_GET_RATE_BY_INTERMEDIATE_CURRENCY)) {
            statement.setString(1, exchangeRequestDto.getBaseCurrencyCode());
            statement.setString(2, exchangeRequestDto.getTargetCurrencyCode());
            statement.setString(3, intermediateCurrencyCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }
    public Optional<ResponseExchangeRateDTO> save(RequestExchangeRateDTO requestExchangeRateDTO) throws SQLException {
        Optional<ResponseExchangeRateDTO> responseExchangeRateDTOOptional = getResponseExchangeRate(requestExchangeRateDTO);
        if (responseExchangeRateDTOOptional.isPresent()) {
            ResponseExchangeRateDTO responseExchangeRate = responseExchangeRateDTOOptional.get();
            try (Connection conn = DatabaseConnection.getConnection()) {
                try (PreparedStatement statement = conn.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, (int) responseExchangeRate.getBaseCurrency().getId());
                    statement.setInt(2, (int) responseExchangeRate.getTargetCurrency().getId());
                    statement.setBigDecimal(3, responseExchangeRate.getRate());
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            responseExchangeRate.setId(generatedKeys.getLong(1));
                            return Optional.of(responseExchangeRate);
                        } else {
                            throw new SQLException("Error when receiving automatically generated keys");
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
    private Optional<ResponseExchangeRateDTO> getResponseExchangeRate(RequestExchangeRateDTO requestExchangeRateDTO) throws SQLException {
        Optional<Currency> base = currencyDao.findByCode(requestExchangeRateDTO.getBaseCurrencyCode());
        Optional<Currency> target = currencyDao.findByCode(requestExchangeRateDTO.getTargetCurrencyCode());
        if (base.isPresent() && target.isPresent()) {
            return Optional.of(new ResponseExchangeRateDTO(base.get(), target.get(), requestExchangeRateDTO.getRate()));
        }
        return Optional.empty();
    }
    public Optional<ResponseExchangeRateDTO> update(RequestExchangeRateDTO requestExchangeRateDTO) throws SQLException {
        Optional<ResponseExchangeRateDTO> responseExchangeRate = getResponseExchangeRate(requestExchangeRateDTO);
        if (responseExchangeRate.isPresent()) {
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SQL_UPDATE, Statement.RETURN_GENERATED_KEYS)) {
                statement.setBigDecimal(1, requestExchangeRateDTO.getRate());
                statement.setString(2, requestExchangeRateDTO.getBaseCurrencyCode());
                statement.setString(3, requestExchangeRateDTO.getTargetCurrencyCode());
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        responseExchangeRate.get().setId(generatedKeys.getLong(1));
                        return responseExchangeRate;
                    }
                }
            }
        }
        return Optional.empty();
    }
}
