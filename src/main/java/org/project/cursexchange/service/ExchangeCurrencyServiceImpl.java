package org.project.cursexchange.service;
import org.project.cursexchange.DependencyFactory;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.ExchangeCurrencyDao;
import org.project.cursexchange.dto.ExchangeCalculationDTO;
import org.project.cursexchange.dto.ExchangeCurrencyDTO;
import org.project.cursexchange.exceptions.CurrencyExchangeNotFound;
import org.project.cursexchange.exceptions.CurrencyExistException;
import org.project.cursexchange.exceptions.CurrencyNotFound;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeCurrencyServiceImpl implements ExchangeCurrencyService {

    private  final ExchangeCurrencyDao exchangeCurrencyDao;
    private  final CurrencyDao currencyDAO;

    public ExchangeCurrencyServiceImpl() {
        this.exchangeCurrencyDao = DependencyFactory.createExchangeCurrencyDao();;
        this.currencyDAO = DependencyFactory.createCurrencyDao();;
    }

    @Override
    public Optional<ExchangeCurrency> getExchangeCurrency(String currencyBaseCode, String currencyTargetCode) {
        try {
            Currency baseCurrency=currencyDAO.findByCode(currencyBaseCode);
            Currency targetCurrency=currencyDAO.findByCode(currencyTargetCode);
            return exchangeCurrencyDao.findCurrencyExchangeById(baseCurrency,targetCurrency);
        }
        catch (SQLException e){
            throw new DataAccesException();
        }
    }

    @Override
    public ExchangeCurrency addExchangeCurrency(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        try {
            if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null || baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || rate.isBlank()) {
                throw new IllegalArgumentException("Данные не могут быть пустыми.");
            }

            if(baseCurrencyCode.equals(targetCurrencyCode)) {
                throw new CurrencyExistException("Невозможно добавить обмен валютами");
            }
            Optional<ExchangeCurrency> exchangeCurrencyOptional=getExchangeCurrency(baseCurrencyCode,targetCurrencyCode);
            if (exchangeCurrencyOptional.isPresent()) {
                throw new CurrencyExistException("Валютная пара уже существует");
            }
            ExchangeCurrencyDTO exchangeCurrencyDto=buildExchangeCurrencyDTO(baseCurrencyCode,targetCurrencyCode,parseDecimal(rate));
            if(!exchangeCurrencyDao.saveCurrencyExchange(exchangeCurrencyDto)){
                throw new DataAccesException();
            }
            return getExchangeCurrency(baseCurrencyCode,targetCurrencyCode).get();
        }
        catch (SQLException e){
            throw new DataAccesException();
        }
    }

    @Override
    public ExchangeCurrency updateExchangeCurrency(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        try {
            String rateChecked=parseDecimal(rate).toString();
            Optional<ExchangeCurrency> exchangeCurrencyOptional=getExchangeCurrency(baseCurrencyCode,targetCurrencyCode);
            if(exchangeCurrencyOptional.isPresent()) {
                ExchangeCurrency exchangeCurrency=exchangeCurrencyOptional.get();
                if(exchangeCurrencyDao.updateRateCurrencyExchange(exchangeCurrency,rateChecked)){
                    return getExchangeCurrency(baseCurrencyCode,targetCurrencyCode).get();
                }
            }
            throw new CurrencyExchangeNotFound();
        }
        catch (SQLException e){
            throw new DataAccesException();
        }
    }

    @Override
    public ExchangeCalculationDTO getExchangeCurrencyWithConvertedAmount(String baseCurrencyCode, String targetCurrencyCode, String amount) {
       try {
           BigDecimal amountByDigit = parseDecimal(amount);
           BigDecimal directRate = getDirectExchangeRate(baseCurrencyCode, targetCurrencyCode);
           if (directRate != null) {
               return buildExchangeCalculationDTO(baseCurrencyCode,
                       targetCurrencyCode,
                       directRate,
                       amountByDigit,
                       directRate.multiply(amountByDigit));
           }

           BigDecimal reverseRate = getReverseExchangeRate(baseCurrencyCode, targetCurrencyCode);
           if (reverseRate != null) {
               return buildExchangeCalculationDTO(baseCurrencyCode,
                       targetCurrencyCode, reverseRate, amountByDigit,
                       amountByDigit.multiply(reverseRate));
           }

           BigDecimal derivedRate = getDerivedExchangeRate(baseCurrencyCode, targetCurrencyCode);
           return buildExchangeCalculationDTO(baseCurrencyCode, targetCurrencyCode,
                   derivedRate, amountByDigit,
                   amountByDigit.multiply(derivedRate));
       }
       catch (SQLException e){
           if(e.getMessage().contains("no such column")){
               throw  new CurrencyExchangeNotFound("Не найдена валютная пара.");
           }
           else{
               throw new DataAccesException();
           }
       }
    }

    private BigDecimal parseDecimal(String amount) {
        try {
            var digit= new BigDecimal(amount);
            if(digit.compareTo(BigDecimal.ZERO)>=0){
                return new BigDecimal(amount).setScale(6, RoundingMode.HALF_UP);
            }
            else {
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

    private Optional<ExchangeCurrency> findCurrencyPair(List<ExchangeCurrency> list1, List<ExchangeCurrency> list2){
        return list1.stream()
                .filter(ec1 -> list2.stream()
                        .anyMatch(ec2 -> ec1.getBaseCurrency().getCode().equals(ec2.getBaseCurrency().getCode()))) // Проверяем совпадение поля baseCurrency
                .findFirst();
    }
    private BigDecimal getDerivedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws CurrencyNotFound, SQLException {
            List<ExchangeCurrency> basesExchange = exchangeCurrencyDao.findCurrencyExchangeByTargetCode(baseCurrencyCode);
            List<ExchangeCurrency> targetsExchange = exchangeCurrencyDao.findCurrencyExchangeByTargetCode(targetCurrencyCode);

            Optional<ExchangeCurrency> commonCurrency=findCurrencyPair(basesExchange,targetsExchange);
            Optional<ExchangeCurrency> commonCurrencyInverse=findCurrencyPair(targetsExchange,basesExchange);
           if (commonCurrency.isPresent() && commonCurrencyInverse.isPresent()) {
               ExchangeCurrency baseExchange=commonCurrency.get();
               ExchangeCurrency targetExchange=commonCurrencyInverse.get();
               return targetExchange.getRate().divide(baseExchange.getRate(), 2, RoundingMode.HALF_UP);
           }
           throw new CurrencyNotFound("Валютная пара не найдена.");
    }

    private BigDecimal getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeCurrency> exchangeCurrency = getExchangeCurrency(baseCurrencyCode, targetCurrencyCode);
        return exchangeCurrency.map(ExchangeCurrency::getRate).orElse(null);
    }

    private ExchangeCalculationDTO buildExchangeCalculationDTO(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new ExchangeCalculationDTO(
                currencyDAO.findByCode(baseCurrencyCode),
                currencyDAO.findByCode(targetCurrencyCode),
                rate,
                amount,
                convertedAmount.setScale(2, RoundingMode.HALF_UP)
        );
    }
    private ExchangeCurrencyDTO buildExchangeCurrencyDTO(String baseCurrencyCode,
                                                         String targetCurrencyCode, BigDecimal rate) {
        return new ExchangeCurrencyDTO(
                currencyDAO.findByCode(baseCurrencyCode),
                currencyDAO.findByCode(targetCurrencyCode),
                rate
        );
    }

}
