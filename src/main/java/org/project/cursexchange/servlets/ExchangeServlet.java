package org.project.cursexchange.servlets;

import org.project.cursexchange.Util;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.dto.ExchangeCalculationDTO;
import org.project.cursexchange.exceptions.CurrencyExchangeNotFound;
import org.project.cursexchange.exceptions.CurrencyExistException;
import org.project.cursexchange.exceptions.CurrencyNotFound;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ErrorResponse;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.service.ExchangeCurrencyServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeCurrencyService exchangeCurrencyService;
    private final String requestParameterFrom="from";
    private final String requestParameterTo="to";
    private final String requestParameterAmount="amount";
    @Override
    public void init() throws ServletException {
        exchangeCurrencyService= new ExchangeCurrencyServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String codeCurrencyFrom = request.getParameter(requestParameterFrom);
            String codeCurrencyTo = request.getParameter(requestParameterTo);
            String amount = request.getParameter(requestParameterAmount);

            ExchangeCalculationDTO exchangeCalculationDTO=exchangeCurrencyService.getExchangeCurrencyWithConvertedAmount(codeCurrencyFrom,
                    codeCurrencyTo,
                    amount);
            response.getWriter().write(Util.convertToJson(exchangeCalculationDTO));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (IllegalArgumentException  e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound|CurrencyNotFound  e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }
}