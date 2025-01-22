package org.project.cursexchange.util;

import org.project.cursexchange.exception.CurrencyNotValidCodeException;

public class CurrencyValidator {
    public static final int CURRENCY_CODE_LENGTH = 3;
    public static final int CURRENCY_NAME_LENGTH = 15;
    public static final int CURRENCY_SIGN_LENGTH = 3;
    public String validateCurrencyCodeLength(String path) throws CurrencyNotValidCodeException {
        if (path == null || path.isEmpty()) {
            throw new CurrencyNotValidCodeException();
        }
        String regex = String.format("^/([A-Za-z]{%d})$", CURRENCY_CODE_LENGTH);
        if (!path.trim().matches(regex)) {
            throw new CurrencyNotValidCodeException();
        }
        return path.substring(1);
    }
    public void validateCurrency(String code, String name, String sign) {
        if (code == null || name == null || sign == null || code.isBlank() || name.isBlank() || sign.isBlank()) {
            throw new IllegalArgumentException("Data can`t be empty.");
        }
        validateCurrencyCode(code);
        validateCurrencyName(name);
        validateCurrencySign(sign);
    }
    private void validateCurrencyField(String field, int maxLength) {
        //более общие повторяющиеся проверки
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Field should has length  not more " + maxLength + " symbol");
        } else if (!isLatinText(field)) {
            throw new IllegalArgumentException("Data should contains english letter.");
        }
    }
    private void validateCurrencyCode(String code) {
        validateCurrencyField(code, CURRENCY_CODE_LENGTH);
        // кастомная проверка
        if (!code.equals(code.toUpperCase())) {
            throw new IllegalArgumentException("Код должен быть заглавными буквами");
        }
    }
    private void validateCurrencyName(String name) {
        validateCurrencyField(name, CURRENCY_NAME_LENGTH);
    }
    private void validateCurrencySign(String sign) {
        validateCurrencyField(sign, CURRENCY_SIGN_LENGTH);
        // кастомная проверка
        if (containsDigit(sign)) {
            throw new IllegalArgumentException("Знак не должен содержать цифр.");
        }
    }
    private boolean isLatinText(String input) {
        return input.matches("^[A-Za-z]+$");
    }
    private boolean containsDigit(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}

