package org.project.cursexchange.util;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRateValidator {
    public BigDecimal getDecimal(String rate) {
        try {
            var digit = new BigDecimal(rate);
            if (digit.compareTo(BigDecimal.ZERO) >= 0) {
                return new BigDecimal(rate).setScale(6, RoundingMode.HALF_UP);
            } else {
                throw new IllegalArgumentException("Field should be positive number.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong format for" + rate);
        }
    }
}
