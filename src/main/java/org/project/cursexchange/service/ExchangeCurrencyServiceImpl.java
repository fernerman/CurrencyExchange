package org.project.cursexchange.service;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.AmountExchangeRatesDTO;
import org.project.cursexchange.dto.ConvertAmountExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;
import org.project.cursexchange.mapper.ExchangeRateWithAmountMapper;
import org.project.cursexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeCurrencyServiceImpl implements ExchangeCurrencyService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeCurrencyServiceImpl(ExchangeRate exchangeRate) {
        exchangeRateDao = new ExchangeRateDao();
    }

    @Override
    public ConvertAmountExchangeRateDTO getExchangeCurrencyWithConvertedAmount(AmountExchangeRatesDTO dto) {
        String baseCode = dto.getBaseCurrencyCode();
        String targetCode = dto.getTargetCurrencyCode();
        BigDecimal amount = dto.getAmount();
        Optional<ExchangeRate> rateDirect = exchangeRateDao.findByCodes(baseCode, targetCode);
        if (rateDirect.isPresent()) {
            return ExchangeRateWithAmountMapper.toDTO(
                    rateDirect.get(),
                    rateDirect.get().getRate(),
                    amount
            );
        }
        Optional<ExchangeRate> reversedRate = exchangeRateDao.findByCodes(targetCode, baseCode);
        if (reversedRate.isPresent()) {
            BigDecimal rate = BigDecimal.ONE.divide(reversedRate.get().getRate(), 2, RoundingMode.HALF_UP);
            return ExchangeRateWithAmountMapper.toDTO(
                    reversedRate.get(),
                    rate,
                    amount
            );
        }
        Optional<ExchangeRate> intermediateCurrency = exchangeRateDao.findRateByIntermediateCurrency(baseCode, targetCode, "USD");
        if (intermediateCurrency.isPresent()) {
            BigDecimal rate = intermediateCurrency.get().getRate();
            return ExchangeRateWithAmountMapper.toDTO(
                    intermediateCurrency.get(),
                    rate,
                    amount
            );
        }
        throw new CurrencyExchangeNotFound();
    }
}
