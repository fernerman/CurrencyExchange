package org.project.cursexchange;

import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;

public class Util {

    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";

    public Util() {
    }
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DEFAULT_DRIVER);
            URL resource = Util.class.getClassLoader().getResource("database.db");
            if (resource == null) {
                throw new RuntimeException("Database file not found in resources folder!");
            }
            try {
                String dbPath = Paths.get(resource.toURI()).toString();
                return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            }
            catch (SQLException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException();
    }
    public static String convertToJson(Object object){
        return new Gson().toJson(object);
    }
}
