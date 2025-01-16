package org.project.cursexchange.servlet;

import org.project.cursexchange.DependencyFactory;
import org.project.cursexchange.Util;

import org.project.cursexchange.exception.*;
import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.service.ExchangeCurrencyServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;;
import java.util.List;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeCurrencyService exchangeCurrencyService;
    private final String baseCurrencyCodeParameter="baseCurrencyCode";
    private final String targetCurrencyCodeParameter="targetCurrencyCode";
    private final String rateParameter="rate";

    @Override
    public void init() throws ServletException {
        exchangeCurrencyService= new ExchangeCurrencyServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            var exchangeCurrencyDao=DependencyFactory.createExchangeCurrencyDao();
            List<ExchangeRate> listOfExchangeRate = exchangeCurrencyDao.findAllCurrencyExchange();
            response.getWriter().write(Util.convertToJson(listOfExchangeRate));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String baseCurrencyCode = request.getParameter(baseCurrencyCodeParameter);
            String targetCurrencyCode = request.getParameter(targetCurrencyCodeParameter);
            String rate = request.getParameter(rateParameter);

            ExchangeRate savedCurrencyExchange=exchangeCurrencyService.addExchangeCurrency(baseCurrencyCode, targetCurrencyCode, rate);
            response.getWriter().write(Util.convertToJson(savedCurrencyExchange));
            response.setStatus(HttpServletResponse.SC_CREATED);
        }

        catch (IllegalArgumentException ex){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
        }
        catch (CurrencyExistException ex){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
        }
        catch (CurrencyNotFound | CurrencyExchangeNotFound ex){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
        }
        catch (DataAccesException ex){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
        }
    }
}
