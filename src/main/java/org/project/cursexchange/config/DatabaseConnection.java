package org.project.cursexchange.config;

import org.project.cursexchange.Util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";
    public static Connection getConnection()   {
        try {
        Class.forName(DEFAULT_DRIVER);
        URL resource = DatabaseConnection.class.getClassLoader().getResource("database.db");
        if (resource == null) {
            throw new RuntimeException("Database file not found in resources folder!");
        }

            String dbPath = Paths.get(resource.toURI()).toString();
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Database file not found in resources folder!");
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database: " + e.getMessage(), e);
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException("Database class not found: " + e.getMessage(), e);
        }
    }
}

