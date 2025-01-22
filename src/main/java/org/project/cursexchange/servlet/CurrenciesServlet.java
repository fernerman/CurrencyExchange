package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.dto.RequestCurrencyDTO;
import org.project.cursexchange.exception.CurrencyAlreadyExistException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.util.CurrencyValidator;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final String REQUEST_PARAMETER_CODE = "code";
    private static final String REQUEST_PARAMETER_NAME = "name";
    private static final String REQUEST_PARAMETER_SIGN = "sign";
    private CurrencyDao currencyDao;
    private CurrencyValidator currencyValidator;
    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDao();
        currencyValidator = new CurrencyValidator();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> allCurrencies = currencyDao.findAll();
            response.getWriter().write(JsonConverter.convertToJson(allCurrencies));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter(REQUEST_PARAMETER_CODE);
            String name = request.getParameter(REQUEST_PARAMETER_NAME);
            String sign = request.getParameter(REQUEST_PARAMETER_SIGN);
            currencyValidator.validateCurrency(code, name, sign);

            RequestCurrencyDTO CurrencySaveRequestDTO = new RequestCurrencyDTO(code, name, sign);
            Currency currency = currencyDao.save(CurrencySaveRequestDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonConverter.convertToJson(currency));
        } catch (CurrencyAlreadyExistException ex) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}