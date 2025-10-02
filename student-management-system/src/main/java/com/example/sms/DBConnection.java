package com.example.sms;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Provides JDBC connections to the MySQL database.
 * Loads settings from src/main/resources/db.properties.
 */
public final class DBConnection {
    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = DBConnection.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load database properties: " + e.getMessage());
        }
    }

    private DBConnection() { }

    /**
     * Returns a new JDBC Connection using properties from db.properties.
     */
    public static Connection getConnection() throws SQLException {
        String url = requireNonEmpty(properties.getProperty("db.url"), "db.url");
        String username = requireNonEmpty(properties.getProperty("db.username"), "db.username");
        String password = Objects.toString(properties.getProperty("db.password"), "");
        return DriverManager.getConnection(url, username, password);
    }

    private static String requireNonEmpty(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required DB property: " + key);
        }
        return value;
    }
}
