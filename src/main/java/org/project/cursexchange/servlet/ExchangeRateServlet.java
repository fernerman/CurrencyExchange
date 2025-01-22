package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.dto.ErrorResponse;
import org.project.cursexchange.dto.RequestExchangeRateDTO;
import org.project.cursexchange.dto.ResponseExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyCodeNotFoundInPath;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.ExchangeRateNotFound;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.util.ExchangeRateValidator;
import org.project.cursexchange.util.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final int CURRENCY_CODE_LENGTH = 3;
    private ExchangeRateDao exchangeRateDao;
    private ExchangeRateValidator exchangeRateValidator;
    @Override
    public void init() {
        exchangeRateValidator = new ExchangeRateValidator();
        exchangeRateDao = new ExchangeRateDao();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            String codesInPath = getCodesFromPath(pathInfo);
            String baseCode = codesInPath.substring(0, CURRENCY_CODE_LENGTH);
            String targetCode = codesInPath.substring(CURRENCY_CODE_LENGTH);

            Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(baseCode, targetCode);
            if (exchangeRate.isEmpty()) {
                throw new ExchangeRateNotFound();
            }
            response.getWriter().write(JsonConverter.convertToJson(exchangeRate.get()));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (CurrencyCodeNotFoundInPath e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (ExchangeRateNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String rateFromUser = getRateFromForm(request);
            String pathInfo = request.getPathInfo();
            String correctPath = getCodesFromPath(pathInfo);
            String baseCode = correctPath.substring(0, CURRENCY_CODE_LENGTH);
            String targetCode = correctPath.substring(CURRENCY_CODE_LENGTH);
            BigDecimal rateByDigit = exchangeRateValidator.getDecimal(rateFromUser);
            RequestExchangeRateDTO requestExchangeRateDTO = new RequestExchangeRateDTO(baseCode, targetCode, rateByDigit);
            Optional<ResponseExchangeRateDTO> exchangeRate = exchangeRateDao.update(requestExchangeRateDTO);
            if (exchangeRate.isEmpty()) {
                throw new ExchangeRateNotFound();
            } else {
                response.getWriter().write(JsonConverter.convertToJson(exchangeRate));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (ExchangeRateNotFound | CurrencyNotFound ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(ex)));
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    private String getCodesFromPath(String path) {
        if (path == null || path.isBlank() || !path.startsWith("/") || path.trim().equals("/")) {
            throw new CurrencyCodeNotFoundInPath();
        }
        path = path.trim();
        int maxLengthCodeTwoCodes = CURRENCY_CODE_LENGTH * 2;
        String regex = String.format("^/([A-Z]{%d})$", maxLengthCodeTwoCodes);
        if (path.matches(regex)) {
            return path.substring(1);
        }
        throw new CurrencyCodeNotFoundInPath();
    }
    private String getRateFromForm(HttpServletRequest request) throws IOException {
        // Reading the request body as a JSON string
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
