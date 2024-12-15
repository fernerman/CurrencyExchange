package org.project.cursexchange;

import com.google.gson.Gson;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dto.CurrencyDto;
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
            if(path==null) {
                List<Currency> allCurrency=new CurrencyDao().findAll();
                proccessResponse(response, allCurrency);
            }
            else if(path.trim().equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            else{
                String code=path.split("/")[1];
                Optional<Currency> d=new CurrencyDao().findByCode(code);
                if(d.isPresent()) {
                    proccessResponse(response, d.get());
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
        catch (Exception e) {
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

            CurrencyDto currencyDto = new CurrencyDto(code, name, sign);
            Optional<Currency> currencySaved =new CurrencyDao().save(currencyDto.toEntity());
            if(currencySaved.isEmpty()){
                response.setStatus(HttpServletResponse.SC_CONFLICT);

            }
            else {
                response.setStatus(HttpServletResponse.SC_CREATED);
                proccessResponse(response, currencySaved.get());
            }
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            proccessResponse(response, e.getMessage());
        }

    }
}