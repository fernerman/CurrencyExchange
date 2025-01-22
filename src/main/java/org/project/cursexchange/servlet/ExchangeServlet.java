package org.project.cursexchange.servlet;

import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.dto.ExchangeRequestDto;
import org.project.cursexchange.dto.ResponseExchangeDTO;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.ExchangeRateNotFound;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.util.ExchangeRateValidator;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private static final String REQUEST_PARAMETER_FROM = "from";
    private static final String REQUEST_PARAMETER_TO = "to";
    private static final String REQUEST_PARAMETER_AMOUNT = "amount";
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final ExchangeCurrencyService exchangeCurrencyService = new ExchangeCurrencyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String codeCurrencyFrom = request.getParameter(REQUEST_PARAMETER_FROM);
            String codeCurrencyTo = request.getParameter(REQUEST_PARAMETER_TO);
            String amount = request.getParameter(REQUEST_PARAMETER_AMOUNT);
            BigDecimal amountByDecimal = exchangeRateValidator.getDecimal(amount);
            ExchangeRequestDto saveExchangeRateDTO = new ExchangeRequestDto(codeCurrencyFrom,
                    codeCurrencyTo,
                    amountByDecimal);
            ResponseExchangeDTO responseExchangeDTO = exchangeCurrencyService.exchangeCurrencies(saveExchangeRateDTO);
            response.getWriter().write(JsonConverter.convertToJson(responseExchangeDTO));
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (ExchangeRateNotFound | CurrencyNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}