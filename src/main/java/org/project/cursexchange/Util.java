package org.project.cursexchange;

import com.google.gson.Gson;

import java.sql.*;

public class Util {

    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";
    private static final String DEFAULT_URL = "jdbc:sqlite:databases/database.db";
    private static final String DEFAULT_USERNAME = "";
    private static final String DEFAULT_PASSWORD = "";

    public Util() {
    }
    public static Connection getConnection() throws SQLException {
        try{
            Class.forName(DEFAULT_DRIVER);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return DriverManager.getConnection(DEFAULT_URL);
    }
    public static String convertToJson(Object object){
        return new Gson().toJson(object);
    }
}
