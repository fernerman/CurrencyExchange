package org.project.cursexchange.servlet;

import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.dto.ConvertAmountExchangeRateDTO;
import org.project.cursexchange.dto.AmountExchangeRatesDTO;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.service.ExchangeRateValidationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final String requestParameterFrom = "from";
    private final String requestParameterTo = "to";
    private final String requestParameterAmount = "amount";
    private ExchangeCurrencyService exchangeCurrencyService;
    private ExchangeRateValidationService exchangeRateValidationService;

    @Override
    public void init() throws ServletException {
        exchangeRateValidationService = new ExchangeRateValidationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String codeCurrencyFrom = request.getParameter(requestParameterFrom);
            String codeCurrencyTo = request.getParameter(requestParameterTo);
            String amount = request.getParameter(requestParameterAmount);
            BigDecimal amountByDecimal = exchangeRateValidationService.getDecimal(amount);
            AmountExchangeRatesDTO saveExchangeRateDTO = new AmountExchangeRatesDTO(codeCurrencyFrom,
                    codeCurrencyTo,
                    amountByDecimal);
            ConvertAmountExchangeRateDTO convertAmountExchangeRateDTO = exchangeCurrencyService.getExchangeCurrencyWithConvertedAmount(saveExchangeRateDTO);
            response.getWriter().write(Util.convertToJson(convertAmountExchangeRateDTO));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }
}