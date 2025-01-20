package org.project.cursexchange.servlet;

import org.project.cursexchange.dao.ExchangeRateDao;
import org.project.cursexchange.util.ErrorResponse;
import org.project.cursexchange.dto.RequestExchangeRateDTO;
import org.project.cursexchange.dto.ResponseExchangeRateDTO;
import org.project.cursexchange.exception.CurrencyCodeNotFoundInPath;
import org.project.cursexchange.exception.CurrencyExchangeNotFound;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.DataAccessException;
import org.project.cursexchange.model.ExchangeRate;
import org.project.cursexchange.service.ExchangeRateValidationService;
import org.project.cursexchange.util.JsonConverter;

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

    private final int MAX_LENGTH_CODE = 3;
    private final String rateParameter = "Rate";
    private ExchangeRateDao exchangeRateDao;
    private ExchangeRateValidationService exchangeRateValidationService;

    @Override
    public void init() throws ServletException {
        exchangeRateValidationService = new ExchangeRateValidationService();
        exchangeRateDao = new ExchangeRateDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            String codesInPath = getCodesFromPath(pathInfo);
            String baseCode = codesInPath.substring(0, MAX_LENGTH_CODE);
            String targetCode = codesInPath.substring(MAX_LENGTH_CODE);
            Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(baseCode, targetCode);
            if (exchangeRate.isPresent()) {
                response.getWriter().write(JsonConverter.convertToJson(exchangeRate));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (CurrencyCodeNotFoundInPath e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String rateFromUser = getRateFromForm(request);
            String pathInfo = request.getPathInfo();
            String correctPath = getCodesFromPath(pathInfo);
            String baseCode = correctPath.substring(0, MAX_LENGTH_CODE);
            String targetCode = correctPath.substring(MAX_LENGTH_CODE);
            BigDecimal rateByDigit = exchangeRateValidationService.getDecimal(rateFromUser);
            RequestExchangeRateDTO requestExchangeRateDTO = new RequestExchangeRateDTO(baseCode, targetCode, rateByDigit);
            ResponseExchangeRateDTO exchangeRate = exchangeRateDao.update(requestExchangeRateDTO);
            response.getWriter().write(JsonConverter.convertToJson(exchangeRate));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (CurrencyExchangeNotFound | CurrencyNotFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonConverter.convertToJson(ErrorResponse.sendError(e)));
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
        int maxLengthCodeTwoCodes = MAX_LENGTH_CODE * 2;
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
