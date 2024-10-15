package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.CheckedOutputStream;

public class UserService {
    public boolean registerUser(String username, String password) {
        Properties prop = Main.readConfiguration("db.properties"); // Читаємо конфігурацію з файлу db.properties

        // Перевірка, чи існує вже користувач з таким логіном
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnector.getConnection(prop);
             PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Користувач з таким логіном уже існує!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Якщо логін не існує, хешуємо пароль і додаємо нового користувача
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            System.out.println("Реєстрація успішна!");
            return true; // Повертаємо true, якщо реєстрація вдалася
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Повертаємо false при помилці
        }
    }

    public boolean authenticateUser(String username, String password) {
        Properties prop = Main.readConfiguration("db.properties");
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, storedHash)) {
                    System.out.println("Авторизація успішна");
                    return true;
                } else {
                    System.out.println("Невірний пароль!");
                }
            } else {
                System.out.println("Невірний логін!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}