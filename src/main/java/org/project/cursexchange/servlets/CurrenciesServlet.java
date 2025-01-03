package org.project.cursexchange.servlets;

import com.google.gson.Gson;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.dto.CurrencyDto;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.Currency;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies/*")
public class MainServlet extends HttpServlet {

    private void proccessResponse(HttpServletResponse response, Object objectToJson) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding("UTF-8");
        String payload = new Gson().toJson(objectToJson);
        PrintWriter out = response.getWriter();
        out.print(payload);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            if(path==null||path.isEmpty()) {
                List<Currency> allCurrency=new CurrencyDaoImpl().findAll();
                proccessResponse(response, allCurrency);
            }
            else{
                String code=path.split("/")[1];
                Optional<Currency> d=new CurrencyDaoImpl().findByCode(code);
                if(d.isPresent()) {
                    proccessResponse(response, d.get());
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
        catch (DataAccesException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            proccessResponse(response, e.getMessage());
        }
    }
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String sign = request.getParameter("sign");

            CurrencyDao currencyDao= new CurrencyDaoImpl();
            CurrencyDto currencyDto = new CurrencyDto(code, name, sign);
            boolean isSavedInDatabase =currencyDao.saveCurrency(currencyDto);

            if(isSavedInDatabase){
                response.setStatus(HttpServletResponse.SC_CREATED);
                Optional<Currency> currency=currencyDao.findByCode(currencyDto.getCode());
                if(currency.isPresent()) {
                    response.getWriter().write(new Gson().toJson(currency.get()));
                }
            }
            else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            proccessResponse(response, e.getMessage());
        }

    }
}