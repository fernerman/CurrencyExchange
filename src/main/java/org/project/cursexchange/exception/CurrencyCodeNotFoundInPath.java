package org.project.cursexchange.exception;

public class CurrencyCodeNotFoundInPath extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "The currency codes of the pair are missing in the address";
    public CurrencyCodeNotFoundInPath() {
        super(DEFAULT_MESSAGE);
    }

}
