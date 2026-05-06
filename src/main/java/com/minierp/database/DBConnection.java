package com.minierp.database;

import com.minierp.config.AppConfig;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            // Ensure the database directory exists
            File dbFile = new File(AppConfig.DB_PATH);
            dbFile.getParentFile().mkdirs();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(AppConfig.DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            System.out.println("[DB] Connected to SQLite: " + AppConfig.DB_PATH);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null || !instance.isValid()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        if (!isValid()) {
            reconnect();
        }
        return connection;
    }

    private boolean isValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void reconnect() {
        try {
            connection = DriverManager.getConnection(AppConfig.DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect to database", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
