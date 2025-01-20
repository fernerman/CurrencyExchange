package org.project.cursexchange.mapper;

import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.dto.ResponseExchangeRateDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRateWithAmountMapper {
    public static ResponseExchangeDTO toDTO(
            ResponseExchangeRateDTO responseExchangeRateDTO,
            BigDecimal amount) {

        return new ResponseExchangeDTO(
                responseExchangeRateDTO.getBaseCurrency(),
                responseExchangeRateDTO.getTargetCurrency(),
                responseExchangeRateDTO.getRate(),
                amount,
                responseExchangeRateDTO.getRate().multiply(amount).setScale(
                        2, RoundingMode.HALF_UP
                )
        );
    }
}
