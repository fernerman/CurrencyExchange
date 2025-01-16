package org.project.cursexchange.servlet;
import org.project.cursexchange.Util;
import org.project.cursexchange.dao.CurrencyDao;
import org.project.cursexchange.dao.CurrencyDaoImpl;
import org.project.cursexchange.exception.CurrencyNotFound;
import org.project.cursexchange.exception.CurrencyNotValidCodeException;
import org.project.cursexchange.exception.DataAccesException;
import org.project.cursexchange.model.Currency;
import org.project.cursexchange.dto.ErrorResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao currencyDao;
    private final int MAX_LENGTH_CODE=3;
    @Override
    public void init() throws ServletException {
        currencyDao = new CurrencyDaoImpl();
    }

    private String isValidCode (String path) throws CurrencyNotValidCodeException {
       if (path == null || path.isEmpty()) {
           throw new CurrencyNotValidCodeException();
       }
        path = path.trim();
        String regex = String.format("^/([A-Za-z]{%d})$", MAX_LENGTH_CODE);
        if (path.matches(regex)) {
            return path.substring(1);
        }
        throw new CurrencyNotValidCodeException();

   }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException  {
       try {
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
