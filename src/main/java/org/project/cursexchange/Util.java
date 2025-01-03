package org.project.cursexchange;

import java.sql.*;

public class Database {

    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";
    private static final String DEFAULT_URL = "jdbc:sqlite:databases/database.db";
    private static final String DEFAULT_USERNAME = "";
    private static final String DEFAULT_PASSWORD = "";

    public Database() {
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
}
