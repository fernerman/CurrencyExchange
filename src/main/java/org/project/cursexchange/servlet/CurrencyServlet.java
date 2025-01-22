package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.CurrencyNotValidCodeException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.util.CurrencyValidator;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private CurrencyDao currencyDao = new CurrencyDao();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            String path = req.getPathInfo();
            String code = currencyValidator.validateCurrencyCodeLength(path);
            Optional<Currency> currencyFoundByCode = currencyDao.findByCode(code);
            if (currencyFoundByCode.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new CurrencyNotFound(code);
            }
            response.getWriter().write(JsonConverter.convertToJson(currencyFoundByCode));
        } catch (CurrencyNotValidCodeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(JsonConverter.convertToJson(ex)));
        } catch (CurrencyNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(JsonConverter.convertToJson(ex)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
