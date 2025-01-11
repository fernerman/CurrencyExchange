package org.project.cursexchange.servlets;

import org.project.cursexchange.Util;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dto.CurrencyDTO;
import org.project.cursexchange.exceptions.CurrencyExistException;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ErrorResponse;

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

    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> allCurrencies =currencyDao.findAll();
            response.getWriter().write(Util.convertToJson(allCurrencies));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String sign = request.getParameter("sign");

            checkParametrsIsCorrect(code,name,sign);
            CurrencyDTO currencyDto = new CurrencyDTO(code, name, sign);
            boolean isSavedCurrency =currencyDao.saveCurrency(currencyDto);
            if(isSavedCurrency){
                response.setStatus(HttpServletResponse.SC_CREATED);
                Currency savedCurrency=currencyDao.findByCode(code);
                response.getWriter().write(Util.convertToJson(savedCurrency));
            }
        }
        catch (CurrencyExistException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(e)));
        }
        catch (DataAccesException e) {
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
    private void checkParametrsIsCorrect( String code,String name,String sign ) throws  IllegalArgumentException{
        int MAX_LENGTH_CODE = 3;
        int MAX_LENGTH_PARAMETRS = 25;
        if (code == null || name == null || sign == null || code.isBlank() || name.isBlank() || sign.isBlank()) {
            throw new IllegalArgumentException("Данные не могут быть пустыми");
        }
        else if(code.length()>MAX_LENGTH_CODE || !code.equals(code.toUpperCase())){
            throw new IllegalArgumentException("Код должен быть заглавными буквами и длиной не более " + MAX_LENGTH_CODE+" символов");
        }
        else if ( name.length()>MAX_LENGTH_PARAMETRS||sign.length()>MAX_LENGTH_PARAMETRS) {
            throw new IllegalArgumentException("Данные не должны превышать длины "+ MAX_LENGTH_PARAMETRS+" символов");
        }
        else if( isDigit(code) || isDigit(name)){
            throw new IllegalArgumentException("Данные должны быть из английских букв");
        }
    }
}