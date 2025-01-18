package org.project.cursexchange.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRateValidationService {

    public BigDecimal getRate(String rate) {
        try {
            var digit = new BigDecimal(rate);
            if (digit.compareTo(BigDecimal.ZERO) >= 0) {
                return new BigDecimal(rate).setScale(6, RoundingMode.HALF_UP);
            } else {
                throw new IllegalArgumentException("Поле не может быть отрицательным");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат для числа" + rate);
        }
    }
}
