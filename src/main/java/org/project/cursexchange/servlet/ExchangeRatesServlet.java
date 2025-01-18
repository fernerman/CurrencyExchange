package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.SaveExchangeRateDTO;
import org.project.cursexchange.exception.*;
import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.service.ExchangeRateValidationService;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;;
import java.math.BigDecimal;
import java.util.List;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateDao exchangeRateDao;
    private ExchangeRateValidationService exchangeRateValidationService;
    private final String baseCurrencyCodeParameter = "baseCurrencyCode";
    private final String targetCurrencyCodeParameter = "targetCurrencyCode";
    private final String rateParameter = "rate";

    @Override
    public void init() throws ServletException {
        exchangeRateDao = new ExchangeRateDao();
        exchangeRateValidationService=new ExchangeRateValidationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            List<ExchangeRate> listOfExchangeRate = exchangeRateDao.findAll();
            response.getWriter().write(JsonConverter.convertToJson(listOfExchangeRate));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String baseCurrencyCode = request.getParameter(baseCurrencyCodeParameter);
            String targetCurrencyCode = request.getParameter(targetCurrencyCodeParameter);
            String rate = request.getParameter(rateParameter);
            BigDecimal rateByDecimal=exchangeRateValidationService.getRate(rate);
            SaveExchangeRateDTO saveExchangeRateDTO=new SaveExchangeRateDTO(baseCurrencyCode, targetCurrencyCode, rateByDecimal);
            long id=exchangeRateDao.save(saveExchangeRateDTO);
            response.getWriter().write(JsonConverter.convertToJson(exchangeRateDao.findById((int)id).get()));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (CurrencyExistException ex) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (CurrencyNotFound | CurrencyExchangeNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (DataAccessException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        }
    }
}
