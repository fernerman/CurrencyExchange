
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/test")
public class MainServlet extends HttpServlet {

    private static final String URL = "jdbc:sqlite:C:/database/database.db";

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        response.setContentType("text/plain");
        try (Connection con = DriverManager.getConnection(URL);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Currencies")) {

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                int id = rs.getInt("id");
                result.append("ID: ").append(id).append("\n");
            }

            response.getWriter().write(result.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }
}