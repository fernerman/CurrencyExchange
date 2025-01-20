package org.project.cursexchange.servlet;

import org.project.cursexchange.util.ErrorResponse;
import org.project.cursexchange.dto.RequestExchangeDTO;
import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.service.ExchangeCurrencyServiceImpl;
import org.project.cursexchange.service.ExchangeRateValidationService;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    //по ТЗ
    private static final String CURRENCY_CODE = "USD";
    private static final String REQUEST_PARAMETER_FROM = "from";
    private static final String REQUEST_PARAMETER_TO = "to";
    private static final String REQUEST_PARAMETER_AMOUNT = "amount";
    private final ExchangeRateValidationService exchangeRateValidationService = new ExchangeRateValidationService();
    private final ExchangeCurrencyService exchangeCurrencyService =new ExchangeCurrencyServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String codeCurrencyFrom = request.getParameter(REQUEST_PARAMETER_FROM);
            String codeCurrencyTo = request.getParameter(REQUEST_PARAMETER_TO);
            String amount = request.getParameter(REQUEST_PARAMETER_AMOUNT);
            BigDecimal amountByDecimal = exchangeRateValidationService.getDecimal(amount);
            RequestExchangeDTO saveExchangeRateDTO = new RequestExchangeDTO(codeCurrencyFrom,
                    codeCurrencyTo,
                    amountByDecimal);
            ResponseExchangeDTO responseExchangeDTO = exchangeCurrencyService.exchangeCurrencies(saveExchangeRateDTO);
            response.getWriter().write(JsonConverter.convertToJson(responseExchangeDTO));
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        }
    }
}