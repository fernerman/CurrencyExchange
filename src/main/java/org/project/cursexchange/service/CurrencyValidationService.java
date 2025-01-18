package org.project.cursexchange.service;

public class CurrencyValidationService {
    public static final int MAX_LENGTH_CODE = 3;
    public static final int MAX_LENGTH_NAME = 15;
    public static final int MAX_LENGTH_SIGN = 3;

    public void validateCurrency(String code, String name, String sign) {
        if (code == null || name == null || sign == null || code.isBlank() || name.isBlank() || sign.isBlank()) {
            throw new IllegalArgumentException("Полет не может быть пустым");
        }
        validateCurrencyCode(code);
        validateCurrencyName(name);
        validateCurrencySign(sign);
    }

    private void validateCurrencyField(String field, int maxLength) {
        //более общие повторяющиеся проверки
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Поле должно быть длиной не более " + maxLength + " символов");
        } else if (!isLatinText(field)) {
            throw new IllegalArgumentException("Данные должны содеражать буквы из английского языка.");
        }
    }

    private void validateCurrencyCode(String code) {
        validateCurrencyField(code, MAX_LENGTH_CODE);
        // кастомная проверка
        if (!code.equals(code.toUpperCase())) {
            throw new IllegalArgumentException("Код должен быть заглавными буквами");
        }
    }

    private void validateCurrencyName(String name) {
        validateCurrencyField(name, MAX_LENGTH_NAME);
    }

    private void validateCurrencySign(String sign) {
        validateCurrencyField(sign, MAX_LENGTH_SIGN);
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

