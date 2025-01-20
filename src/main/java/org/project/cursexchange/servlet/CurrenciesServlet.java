package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dto.RequestCurrencyDTO;
import org.project.cursexchange.exception.CurrencyExistException;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.util.ErrorResponse;
import org.project.cursexchange.service.CurrencyValidationService;
import org.project.cursexchange.util.JsonConverter;

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
    private CurrencyValidationService currencyValidationService;
    private final String requestParameterCode = "code";
    private final String requestParameterName = "name";
    private final String requestParameterSign = "sign";

    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDao();
        currencyValidationService= new CurrencyValidationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> allCurrencies = currencyDao.findAll();
            response.getWriter().write(JsonConverter.convertToJson(allCurrencies));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter(requestParameterCode);
            String name = request.getParameter(requestParameterName);
            String sign = request.getParameter(requestParameterSign);
            currencyValidationService.validateCurrency(code, name, sign);
            RequestCurrencyDTO CurrencySaveRequestDTO = new RequestCurrencyDTO( code, name, sign);
            Currency currency = currencyDao.save(CurrencySaveRequestDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonConverter.convertToJson(currency));
        } catch (CurrencyExistException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        }
    }
}