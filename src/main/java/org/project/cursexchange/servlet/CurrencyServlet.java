package org.project.cursexchange.servlet;

import org.project.cursexchange.util.JsonConverter;
import org.project.cursexchange.dao.Dao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.CurrencyNotValidCodeException;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.dto.ErrorResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private Dao<Currency> dao;
    private final int MAX_LENGTH_CODE = 3;

    @Override
    public void init() throws ServletException {
        dao = new CurrencyDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            String path = req.getPathInfo();
            String code = getValidCode(path);
            Optional<Currency> currencyFoundByCode = dao.findByCode(code);
            if (currencyFoundByCode.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new CurrencyNotFound(code);
            }
            response.getWriter().write(JsonConverter.convertToJson(currencyFoundByCode));
        } catch (CurrencyNotValidCodeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (CurrencyNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (DataAccessException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        }
    }

    private String getValidCode(String path) throws CurrencyNotValidCodeException {
        if (path == null || path.isEmpty()) {
            throw new CurrencyNotValidCodeException();
        }
        String regex = String.format("^/([A-Za-z]{%d})$", MAX_LENGTH_CODE);
        if (path.trim().matches(regex)) {
            return path.substring(1);
        }
        throw new CurrencyNotValidCodeException();
    }
}
