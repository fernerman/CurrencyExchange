package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.DataAccesException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.dto.ErrorResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao currencyDao;
    private final String requestParameterCode = "code";
    private final String requestParameterName = "name";
    private final String requestParameterSign = "sign";

    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> allCurrencies = currencyDao.findAll();
            response.getWriter().write(Util.convertToJson(allCurrencies));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter(requestParameterCode);
            String name = request.getParameter(requestParameterName);
            String sign = request.getParameter(requestParameterSign);

            checkParametersIsCorrect(code, name, sign);
            CurrencyDTO currencyDto = new CurrencyDTO(code, name, sign);
            boolean isSavedCurrency = currencyDao.saveCurrency(currencyDto);
            if (isSavedCurrency) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                Currency savedCurrency = currencyDao.findByCode(code);
                response.getWriter().write(Util.convertToJson(savedCurrency));
            }
        } catch (CurrencyExistException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    public boolean isDigit(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLatinAlphabetOnly(String input) {
        return input != null && input.matches("^[A-Za-z]+$");
    }

    private void checkParametersIsCorrect(String code, String name, String sign) throws IllegalArgumentException {
        int maxLengthCode = 3;
        int maxLengthParameters = 25;

        if (code == null || name == null || sign == null || code.isBlank() || name.isBlank() || sign.isBlank()) {
            throw new IllegalArgumentException("Данные не могут быть пустыми");
        } else if (code.length() > maxLengthCode || !code.equals(code.toUpperCase())) {
            throw new IllegalArgumentException("Код должен быть заглавными буквами и длиной не более " + maxLengthCode + " символов");
        } else if (name.length() > maxLengthParameters || sign.length() > maxLengthParameters) {
            throw new IllegalArgumentException("Данные не должны превышать длины " + maxLengthParameters + " символов");
        } else if (!isLatinAlphabetOnly(code) || !isLatinAlphabetOnly(code) || isDigit(sign)) {
            throw new IllegalArgumentException("Данные должны содеражать буквы из английского языка и не иметь цифр.");
        }
    }
}