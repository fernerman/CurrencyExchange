package org.project.cursexchange;

import com.google.gson.Gson;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dao.ExchangeCurrencyDao;
import org.project.cursexchange.dao.ExchangeCurrencyDaoImpl;
import org.project.cursexchange.mappers.ExchangeCurrencyMapper;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {
    private CurrencyDao currencyDao;
    private ExchangeCurrencyDao exchangeCurrencyDao;
    private ExchangeCurrencyMapper exchangeCurrencyMapper;

    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDaoImpl();
        exchangeCurrencyMapper=new ExchangeCurrencyMapper(currencyDao);
        exchangeCurrencyDao=new ExchangeCurrencyDaoImpl(currencyDao,exchangeCurrencyMapper);
    }

    private String getCorrectPath(String path) {
        String emptyResult = "";
        if (path == null || path.trim().equals("/")) {
            return emptyResult;
        }
        String[] parts = path.split("/");
        if (parts.length > 1) {
            String partPath = parts[1];
            if (partPath.length() == 6) {
                return partPath;
            }
        }
        return emptyResult;
    }

    private Optional<ExchangeCurrency> getCurrencyExchange(String baseCode, String targetCode) throws SQLException {
        Optional<Currency> baseCurrency=currencyDao.findByCode(baseCode);
        Optional<Currency> targetCurrency=currencyDao.findByCode(targetCode);
        return exchangeCurrencyDao.findCurrencyExchange(baseCurrency.get(),targetCurrency.get());
    }
    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        try {
            String rateFromUser = request.getParameter("rate");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String pathInfo=request.getPathInfo();
            String correctPath=getCorrectPath(pathInfo);
            if (!correctPath.isEmpty()) {
                String baseCode = correctPath.substring(0, 3);
                String targetCode = correctPath.substring(3);
                Optional<ExchangeCurrency> currencyExchange=getCurrencyExchange(baseCode,targetCode);
                if (currencyExchange.isPresent()) {
                    Optional<ExchangeCurrency> exchangeCurrencyUpdated=exchangeCurrencyDao.updateCurrencyExchange(currencyExchange.get(),rateFromUser);
                    if(exchangeCurrencyUpdated.isPresent()) {
                        response.getWriter().write(new Gson().toJson(exchangeCurrencyUpdated.get()));
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                    else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if(!getCorrectPath(pathInfo).isEmpty()){
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                CurrencyDaoImpl currencyDao = new CurrencyDaoImpl();
                ExchangeCurrencyMapper exchangeCurrencyMapper = new ExchangeCurrencyMapper(currencyDao);
                ExchangeCurrencyDao exchangeCurrencyDao = new ExchangeCurrencyDaoImpl(currencyDao, exchangeCurrencyMapper);

                String codesOfExchangeRates = req.getPathInfo().split("/")[1];
                String baseCode = codesOfExchangeRates.substring(0, 3);
                String targetCode = codesOfExchangeRates.substring(3);

                Optional<Currency> baseCurrency=currencyDao.findByCode(baseCode);
                Optional<Currency> targetCurrency=currencyDao.findByCode(targetCode);
                if (baseCurrency.isPresent()&&targetCurrency.isPresent()) {
                    Optional<ExchangeCurrency> currencyExchange=exchangeCurrencyDao.findCurrencyExchange(baseCurrency.get(),targetCurrency.get());
                    resp.getWriter().write(new Gson().toJson(currencyExchange));
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
            else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Проверяем метод запроса
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            try {
                doPatch(req, resp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            super.service(req, resp);
        }
    }
}
