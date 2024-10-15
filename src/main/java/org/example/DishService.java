package org.example;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DishService {
    public void addDish(String type, String name, BigDecimal price, Date expiryDate, Properties prop) {
        String sql = "INSERT INTO dishes (type, name, price, expiry_date) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setString(2, name);
            pstmt.setBigDecimal(3, price);
            pstmt.setDate(4, expiryDate);
            pstmt.executeUpdate();
            System.out.println("Страву успішно додано!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDish(int id, Properties prop) {
        String sql = "DELETE FROM dishes WHERE dish_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Страву успішно видалено!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dish getDishByName(String dishName, Properties prop) {
        String sql = "SELECT * FROM dishes WHERE name = ?";
        Dish dish = null;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dishName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Отримання значень з ResultSet
                String type = rs.getString("type");
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");
                java.sql.Date sqlExpiryDate = rs.getDate("expiry_date");
                // Перетворення java.sql.Date на java.time.LocalDate
                LocalDate expiryDate = sqlExpiryDate != null ? sqlExpiryDate.toLocalDate() : null;

                // Створення об'єкта Dish
                dish = new Dish(rs.getInt("dish_id"), type, name, price, expiryDate);
            } else {
                System.out.println("Страву не знайдено.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dish;
    }

    public List<Dish> getAllDishes(Properties prop) {
        String sql = "SELECT * FROM dishes";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection(prop);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Отримання метаданих
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Виведення даних про страви
            while (rs.next()) {
                // Виводимо кожне значення з назвою стовпця
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    System.out.println(columnName + " : " + columnValue);
                }
                System.out.println("--------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    public void updateDish(Dish dish, Properties prop) {
        String sql = "UPDATE dishes SET type = ?, name = ?, price = ?, expiry_date = ? WHERE dish_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dish.getType());
            pstmt.setString(2, dish.getName());
            pstmt.setBigDecimal(3, dish.getPrice());
            pstmt.setDate(4, Date.valueOf(dish.getExpiryDate()));
            pstmt.setInt(5, dish.getId());

            int RowsAffected = pstmt.executeUpdate();
            if (RowsAffected > 0) {
                System.out.println("Страву " + dish.getName() + " успішно змінено!");
            } else {
                System.out.println("Страву не знайдено!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}


