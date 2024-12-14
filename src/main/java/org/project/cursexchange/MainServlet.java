package org.project.cursexchange;

import com.google.gson.Gson;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ExchangeCurrency;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies")
public class MainServlet extends HttpServlet {

    private void proccessResponse(HttpServletResponse response, String payload) throws ServletException, IOException {
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding("UTF-8");
        try{
           response.setStatus(HttpServletResponse.SC_OK);
            if(payload != null){
                PrintWriter out = response.getWriter();
                out.print(payload);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder stringBuilder=new StringBuilder();
        try {
            List<Currency> allCurrency=new CurrencyDao().findAll();
            stringBuilder.append(new Gson().toJson(allCurrency));
            proccessResponse(response, stringBuilder.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}