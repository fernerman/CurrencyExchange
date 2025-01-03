package org.project.cursexchange.servlets;

import org.project.cursexchange.Util;
import org.project.cursexchange.dao.ExchangeCurrencyDao;
import org.project.cursexchange.exceptions.CurrencyCodeNotFoundInPath;
import org.project.cursexchange.exceptions.CurrencyNotFound;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.exceptions.CurrencyExchangeNotFound;

import org.project.cursexchange.models.ErrorResponse;
import org.project.cursexchange.models.ExchangeCurrency;
import org.project.cursexchange.service.ExchangeCurrencyService;
import org.project.cursexchange.service.ExchangeCurrencyServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {

    private ExchangeCurrencyService exchangeCurrencyService;
    private ExchangeCurrencyDao exchangeCurrencyDao;

    @Override
    public void init() throws ServletException {
        exchangeCurrencyService= new ExchangeCurrencyServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/json; charset=UTF-8");
            String pathInfo = req.getPathInfo();
            String codesInPath= validateCodesInPath(pathInfo);
            String baseCode = codesInPath.substring(0, 3);
            String targetCode = codesInPath.substring(3);

            Optional<ExchangeCurrency> exchangeCurrency=exchangeCurrencyService.getExchangeCurrency(baseCode,targetCode);
            if(exchangeCurrency.isPresent()) {
                response.getWriter().write(Util.convertToJson(exchangeCurrency));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }

        catch (CurrencyCodeNotFoundInPath e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }


    }
    private String validateCodesInPath(String path) {

        if (path == null || path.trim().equals("/")) {
           throw  new CurrencyCodeNotFoundInPath();
        }
        path = path.trim();
        if (!path.startsWith("/")) {
            throw new CurrencyCodeNotFoundInPath();
        }
        String[] parts = path.split("/");
        if (parts.length == 2) {
            String partPath = parts[1];
            if (partPath.length() == 6) {
                return partPath;
            }
        }
        throw  new CurrencyCodeNotFoundInPath();
    }
private boolean isValidRate(String rate) {
    try {
        new BigDecimal(rate);  // Пробуем преобразовать строку в BigDecimal
        return true;  // Если преобразование успешно, значит это число
    } catch (NumberFormatException e) {
        return false;  // Если ошибка, значит строка не является числом
    }
}


private String getRateFromForm(HttpServletRequest request) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    // Чтение всех строк запроса
    while ((line = request.getReader().readLine()) != null) {
        sb.append(line);
    }

    String rate = sb.toString();
        if (isValidRate(rate)) {
            return rate;
        }
        else {
            throw new IllegalArgumentException("Invalid rate value: " + rate);
        }
    }


    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/json; charset=UTF-8");
            String rateFromUser =getRateFromForm(request);
            String pathInfo=request.getPathInfo();
            String correctPath= validateCodesInPath(pathInfo);

            String baseCode = correctPath.substring(0, 3);
            String targetCode = correctPath.substring(3);

            ExchangeCurrency updateExchangeCurrency=exchangeCurrencyService.updateExchangeCurrency(baseCode,targetCode,rateFromUser);
            response.getWriter().write(Util.convertToJson(updateExchangeCurrency));
            response.setStatus(HttpServletResponse.SC_OK);
        }

        catch (IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }

        catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Проверяем метод запроса
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        }
        else {
            super.service(req, resp);
        }
    }
}
