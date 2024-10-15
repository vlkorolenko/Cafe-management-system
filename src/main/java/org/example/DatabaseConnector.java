package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    // Метод для отримання з'єднання
    public static Connection getConnection(Properties prop) throws SQLException {
        return DriverManager.getConnection(
                prop.getProperty("url"),   // URL бази даних
                prop.getProperty("username"), // Ім'я користувача
                prop.getProperty("password")); // Пароль
    }
}