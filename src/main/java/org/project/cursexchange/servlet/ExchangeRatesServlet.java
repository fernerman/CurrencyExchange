package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.dto.RequestExchangeRateDTO;
import org.project.cursexchange.dto.ResponseExchangeRateDTO;
import org.project.cursexchange.exception.ExchangeRateNotFound;
import org.project.cursexchange.exception.CurrencyAlreadyExistException;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.util.ExchangeRateValidator;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final String BASE_CURRENCY_CODE_PARAMETER = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_PARAMETER = "targetCurrencyCode";
    private static final String RATE_PARAMETER = "rate";
    private  final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private  final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            List<ExchangeRate> listOfExchangeRate = exchangeRateDao.findAll();
            response.getWriter().write(JsonConverter.convertToJson(listOfExchangeRate));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String baseCurrencyCode = request.getParameter(BASE_CURRENCY_CODE_PARAMETER);
            String targetCurrencyCode = request.getParameter(TARGET_CURRENCY_CODE_PARAMETER);
            String rate = request.getParameter(RATE_PARAMETER);
            BigDecimal rateByDecimal = exchangeRateValidator.getDecimal(rate);

            RequestExchangeRateDTO requestExchangeRateDTO = new RequestExchangeRateDTO(baseCurrencyCode, targetCurrencyCode, rateByDecimal);
            Optional<ResponseExchangeRateDTO> exchangeRate = exchangeRateDao.save(requestExchangeRateDTO);
            if(exchangeRate.isEmpty()) {
                throw new ExchangeRateNotFound();
            }
            response.getWriter().write(JsonConverter.convertToJson(exchangeRate));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (CurrencyAlreadyExistException ex) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (CurrencyNotFound | ExchangeRateNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
