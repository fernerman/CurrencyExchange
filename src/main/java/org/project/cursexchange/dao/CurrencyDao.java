package org.project.cursexchange.dao;

import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.model.Currency;

import java.util.List;
import java.util.Optional;


public interface CurrencyDao
{
    Optional<Currency> findById(int id) ;
    Currency findByCode(String code) ;
    List<Currency> findAll();
    boolean saveCurrency(CurrencyDTO currencyDto) ;
}
