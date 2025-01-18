package org.project.cursexchange.service;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.ExchangeCalculationDTO;
import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeCurrencyServiceImpl implements ExchangeCurrencyService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeCurrencyServiceImpl(ExchangeRate exchangeRate) {
        exchangeRateDao = new ExchangeRateDao();
    }


    @Override
    public ExchangeCalculationDTO getExchangeCurrencyWithConvertedAmount(String baseCurrencyCode, String targetCurrencyCode, String amount) {
        try {
            BigDecimal amountByDigit = parseDecimal(amount);
            BigDecimal directRate = getDirectExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (directRate != null) {
                return buildExchangeCalculationDTO(baseCurrencyCode, targetCurrencyCode, directRate, amountByDigit, directRate.multiply(amountByDigit));
            }

            BigDecimal reverseRate = getReverseExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (reverseRate != null) {
                return buildExchangeCalculationDTO(baseCurrencyCode, targetCurrencyCode, reverseRate, amountByDigit, amountByDigit.multiply(reverseRate));
            }

            BigDecimal derivedRate = getDerivedExchangeRate(baseCurrencyCode, targetCurrencyCode);
            return buildExchangeCalculationDTO(baseCurrencyCode, targetCurrencyCode, derivedRate, amountByDigit, amountByDigit.multiply(derivedRate));
        } catch (SQLException e) {
            if (e.getMessage().contains("no such column")) {
                throw new CurrencyExchangeNotFound("Не найдена валютная пара.");
            } else {
                throw new DataAccessException();
            }
        }
    }

    private BigDecimal parseDecimal(String amount) {
        try {
            var digit = new BigDecimal(amount);
            if (digit.compareTo(BigDecimal.ZERO) >= 0) {
                return new BigDecimal(amount).setScale(6, RoundingMode.HALF_UP);
            } else {
                throw new IllegalArgumentException("Поле не может быть отрицательным");
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат для числа" + amount);
        }
    }

    private BigDecimal getDirectExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        return getExchangeRate(baseCurrencyCode, targetCurrencyCode);
    }

    private BigDecimal getReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        BigDecimal reverseRate = getDirectExchangeRate(targetCurrencyCode, baseCurrencyCode);
        return reverseRate != null ? BigDecimal.ONE.divide(reverseRate, 2, RoundingMode.HALF_UP) : null;
    }

    private Optional<ExchangeRate> findCurrencyPair(List<ExchangeRate> list1, List<ExchangeRate> list2) {
        return list1.stream().filter(ec1 -> list2.stream().anyMatch(ec2 -> ec1.getBaseCurrency().getCode().equals(ec2.getBaseCurrency().getCode()))) // Проверяем совпадение поля baseCurrency
                .findFirst();
    }

    private BigDecimal getDerivedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws CurrencyNotFound, SQLException {
        List<ExchangeRate> basesExchange = exchangeCurrencyDao.findCurrencyExchangeByTargetCode(baseCurrencyCode);
        List<ExchangeRate> targetsExchange = exchangeCurrencyDao.findCurrencyExchangeByTargetCode(targetCurrencyCode);

        Optional<ExchangeRate> commonCurrency = findCurrencyPair(basesExchange, targetsExchange);
        Optional<ExchangeRate> commonCurrencyInverse = findCurrencyPair(targetsExchange, basesExchange);
        if (commonCurrency.isPresent() && commonCurrencyInverse.isPresent()) {
            ExchangeRate baseExchange = commonCurrency.get();
            ExchangeRate targetExchange = commonCurrencyInverse.get();
            return targetExchange.getRate().divide(baseExchange.getRate(), 2, RoundingMode.HALF_UP);
        }
        throw new CurrencyNotFound("Валютная пара не найдена.");
    }

    private BigDecimal getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> exchangeCurrency = findByCodes(baseCurrencyCode, targetCurrencyCode);
        return exchangeCurrency.map(ExchangeRate::getRate).orElse(null);
    }

    private ExchangeCalculationDTO buildExchangeCalculationDTO(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new ExchangeCalculationDTO(DAO.findByCode(baseCurrencyCode), DAO.findByCode(targetCurrencyCode), rate, amount, convertedAmount.setScale(2, RoundingMode.HALF_UP));
    }

}
