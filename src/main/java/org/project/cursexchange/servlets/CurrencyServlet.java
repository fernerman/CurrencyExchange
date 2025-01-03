package org.project.cursexchange.servlets;
import org.project.cursexchange.Util;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.exceptions.CurrencyNotFound;
import org.project.cursexchange.exceptions.CurrencyNotValidCodeException;
import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.Currency;
import org.project.cursexchange.models.ErrorResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao currencyDao;

    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDaoImpl();
    }

    private String isValidCode (String path) throws CurrencyNotValidCodeException {
       if (path == null || path.isEmpty()) {
           throw new CurrencyNotValidCodeException();
       }
       String[] parts = path.split("/");

       if (parts.length == 2 && parts[1].length()==3) {
           return parts[1];
       }
        throw new CurrencyNotValidCodeException();

   }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException  {
       try {
           response.setContentType("application/json; charset=UTF-8");
           String path=req.getPathInfo();
           String code= isValidCode(path);

           Currency currencyFoundByCode=currencyDao.findByCode(code);
           response.setStatus(HttpServletResponse.SC_OK);
           response.getWriter().write(Util.convertToJson(currencyFoundByCode));
       }

       catch (CurrencyNotValidCodeException ex){

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
        }
       catch (CurrencyNotFound ex){
           response.setStatus(HttpServletResponse.SC_NOT_FOUND);
           response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
       }
       catch (DataAccesException ex){
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           response.getWriter().write(Util.convertToJson(ErrorResponse.sendError(ex)));
       }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
