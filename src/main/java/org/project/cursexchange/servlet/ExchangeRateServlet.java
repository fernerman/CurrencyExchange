package org.project.cursexchange.servlet;

import org.project.cursexchange.exception.CurrencyCodeNotFoundInPath;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccesException;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;

import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.model.ExchangeRate;
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
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeCurrencyService exchangeCurrencyService;
    private final int MAX_LENGTH_CODE = 3;

    @Override
    public void init() throws ServletException {
        exchangeCurrencyService = new ExchangeCurrencyServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            String codesInPath = validateCodesInPath(pathInfo);
            String baseCode = codesInPath.substring(0, MAX_LENGTH_CODE);
            String targetCode = codesInPath.substring(MAX_LENGTH_CODE);

            Optional<ExchangeRate> exchangeCurrency = exchangeCurrencyService.getExchangeCurrency(baseCode, targetCode);
            if (exchangeCurrency.isPresent()) {
                response.getWriter().write(Util.convertToJson(exchangeCurrency));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (CurrencyCodeNotFoundInPath e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }


    }

    private String validateCodesInPath(String path) {
        if (path == null || path.isBlank() || !path.startsWith("/") || path.trim().equals("/")) {
            throw new CurrencyCodeNotFoundInPath();
        }
        path = path.trim();
        int maxLengthCodeTwoCodes = MAX_LENGTH_CODE * 2;
        String regex = String.format("^/([A-Za-z]{%d})$", maxLengthCodeTwoCodes);
        if (path.matches(regex)) {
            return path.substring(1);
        }

        throw new CurrencyCodeNotFoundInPath();
    }

    private boolean isValidRate(String rate) {
        try {
            new BigDecimal(rate);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
        } else {
            throw new IllegalArgumentException("Invalid rate value: " + rate);
        }
    }


    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String rateFromUser = getRateFromForm(request);
            String pathInfo = request.getPathInfo();
            String correctPath = validateCodesInPath(pathInfo);

            String baseCode = correctPath.substring(0, MAX_LENGTH_CODE);
            String targetCode = correctPath.substring(MAX_LENGTH_CODE);

            ExchangeRate updateExchangeRate = exchangeCurrencyService.updateExchangeCurrency(baseCode, targetCode, rateFromUser);
            response.getWriter().write(Util.convertToJson(updateExchangeRate));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Проверяем метод запроса
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}
