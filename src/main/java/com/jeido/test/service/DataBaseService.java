package com.jeido.test.service;

import com.jeido.test.Config;
import com.jeido.test.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseService {

    private static DataBaseService instance;

    private Connection conn;
    private final String url;
    private final String user;
    private final String password;


    public static DataBaseService getInstance() {
        if (instance == null) {
            instance = new DataBaseService(Config.dbHost, Config.dbPort, Config.dbName, Config.dbUser, Config.dbPass);
        }
        return instance;
    }

    private void checkDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Test.LOGGER.error("Database driver not found", e);
        }
    }

    private DataBaseService(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        checkDriver();
    }

    private DataBaseService(String host, int port, String name, String user, String password) {
        this("jdbc:mysql://" + host + ":" + port + "/" + name, user, password);
    }

    public Connection getConnection() {
        if (conn != null) return conn;
        try {
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            if (conn != null) closeConnection();
            Test.LOGGER.error("Database connection failed", e);
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            Test.LOGGER.error("Failed to close database connection!", e);
        }
    }



}
