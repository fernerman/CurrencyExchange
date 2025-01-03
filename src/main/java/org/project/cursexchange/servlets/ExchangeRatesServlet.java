package org.project.cursexchange.servlets;

import com.google.gson.Gson;
import org.project.cursexchange.Util;
import org.project.cursexchange.dao.*;
import org.project.cursexchange.dto.ExchangeCurrencyDto;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.mappers.ExchangeCurrencyMapper;
import org.project.cursexchange.models.ErrorResponse;
import org.project.cursexchange.models.ExchangeCurrency;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeServlet  extends HttpServlet {
    private CurrencyDao currencyDao;
    private ExchangeCurrencyMapper exchangeCurrencyMapper;
    private ExchangeCurrencyDao exchangeCurrencyDao;
    @Override
    public void init() throws ServletException {
         currencyDao = new CurrencyDaoImpl();
         exchangeCurrencyMapper=new ExchangeCurrencyMapper(currencyDao);
         exchangeCurrencyDao=new ExchangeCurrencyDaoImpl(currencyDao, exchangeCurrencyMapper);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json; charset=UTF-8");
            List<ExchangeCurrency> listOfExchangeCurrency = exchangeCurrencyDao.findAll();
            response.getWriter().write(Util.convertToJson(listOfExchangeCurrency));
            response.setStatus(HttpServletResponse.SC_OK);
        }

        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
     try {
         String baseCurrencyCode = request.getParameter("baseCurrencyCode");
         String targetCurrencyCode = request.getParameter("targetCurrencyCode");
         String rate = request.getParameter("rate");
         var savedCurrencyExchange = getExchangeCurrency(baseCurrencyCode, targetCurrencyCode, rate);
         if(savedCurrencyExchange.isPresent())
         {
             resp.getWriter().write(new Gson().toJson(savedCurrencyExchange.get()));
             resp.setStatus(HttpServletResponse.SC_CREATED);
         }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
     }
     catch (SQLException e){
         resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
     }
    }

    private static Optional<ExchangeCurrency> getExchangeCurrency(String baseCurrencyCode, String targetCurrencyCode, String rate) throws SQLException {
        ExchangeCurrencyDto exchangeCurrencyDto = new ExchangeCurrencyDto(baseCurrencyCode, targetCurrencyCode,new BigDecimal(rate));
        CurrencyDao currencyDao = new CurrencyDaoImpl();
        ExchangeCurrencyMapper exchangeCurrencyMapper = new ExchangeCurrencyMapper(currencyDao);
        ExchangeCurrencyDao exchangeCurrencyDao = new ExchangeCurrencyDaoImpl(currencyDao, exchangeCurrencyMapper);
        return exchangeCurrencyDao.saveExchangeCurrency(exchangeCurrencyDto);
    }
}

