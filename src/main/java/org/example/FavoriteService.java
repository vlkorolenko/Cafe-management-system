package org.example;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FavoriteService {
    public void getFavoriteByClientID(int clientId, Properties prop) {
        String sql = """
            SELECT d.dish_id, d.name, d.price 
            FROM favorites fd
            JOIN dishes d ON fd.dish_id = d.dish_id
            WHERE fd.client_id = ?
            """;

        List<Dish> favoriteDishes = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = new Dish(rs.getInt("dish_id"), rs.getString("name"), rs.getBigDecimal("price"));
                    favoriteDishes.add(dish);
                }
            }
            ClientService clientService = new ClientService();
            Client client = clientService.getClientById(clientId, prop);
            // Виведення інформації про клієнта та його улюблені страви
            System.out.println("Улюблені страви для клієнта " + client.getFullname() + ":");
            if (favoriteDishes.isEmpty()) {
                System.out.println("У цього клієнта немає улюблених страв.");
            } else {
                for (Dish favDish : favoriteDishes) {
                    System.out.println(favDish.getName() + " - " + favDish.getPrice() + " грн");
                }
            }
            System.out.println("--------------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFavorite(int clientId, String dishName, Properties prop) {
        String sql = "INSERT INTO favorites(client_id, dish_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            DishService dishService = new DishService();
            Dish dish = dishService.getDishByName(dishName, prop);
            pstmt.setInt(1, clientId);
            pstmt.setInt(2, dish.getId());
            pstmt.executeUpdate();
            ClientService clientService = new ClientService();
            Client client = clientService.getClientByIdNoOutput(clientId, prop);
            System.out.println("Улюблена страва додана до клієнта: " + client.getFullname() + "!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllClientsWithFavorites(Properties prop) {
        String sql = """
                SELECT c.client_id, c.full_name AS client_name, d.dish_id, d.name AS dish_name, d.price
                FROM clients c
                LEFT JOIN favorites fd ON c.client_id = fd.client_id
                LEFT JOIN dishes d ON fd.dish_id = d.dish_id
                ORDER BY c.client_id;
                """;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int currentClientId = -1;
            String currentClientName = null;
            List<String> currentFavoriteDishes = new ArrayList<>();

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String dishName = rs.getString("dish_name");
                BigDecimal dishPrice = rs.getBigDecimal("price");

                if (clientId != currentClientId) {
                    // Якщо це не перший клієнт і в поточного клієнта вже є улюблені страви, виводимо їх
                    if (currentClientId != -1) {
                        System.out.println("Клієнт: " + currentClientName);
                        if (!currentFavoriteDishes.isEmpty()) {
                            System.out.println("Улюблені страви:");
                            currentFavoriteDishes.forEach(System.out::println);
                        } else {
                            System.out.println("Немає улюблених страв.");
                        }
                        System.out.println("-----------------------");
                    }

                    // Оновлюємо інформацію про поточного клієнта
                    currentClientId = clientId;
                    currentClientName = clientName;
                    currentFavoriteDishes = new ArrayList<>();
                }

                // Додаємо страву до списку улюблених, якщо вона є
                if (dishName != null) {
                    currentFavoriteDishes.add(dishName + " - " + dishPrice + " грн");
                }
            }

            // Виводимо останнього клієнта
            if (currentClientId != -1) {
                System.out.println("Клієнт: " + currentClientName);
                if (!currentFavoriteDishes.isEmpty()) {
                    System.out.println("Улюблені страви:");
                    currentFavoriteDishes.forEach(System.out::println);
                } else {
                    System.out.println("Немає улюблених страв.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFavorite(String clientName, String dishName, Properties prop) {
        String sql = "DELETE FROM favorites WHERE client_id = ? AND dish_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ClientService clientService = new ClientService();
            DishService dishService = new DishService();
            Dish dish = dishService.getDishByName(dishName, prop);
            Client client = clientService.getClientByNameNoOutput(clientName, prop);
            pstmt.setInt(1, client.getId());
            pstmt.setInt(2, dish.getId());
            pstmt.executeUpdate();
            System.out.println("Страву " + dishName + "успішно видалено зі списку улюблених страв " + client.getFullname() + "!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void printClientsSortedByFavoriteDishOrDrink(Properties prop) {
        String sql = "SELECT c.client_id, c.full_name AS client_name, d.name AS favorite_dish_or_drink " +
                "FROM clients c " +
                "JOIN favorites f ON c.client_id = f.client_id " +
                "JOIN dishes d ON f.dish_id = d.dish_id " +
                "ORDER BY favorite_dish_or_drink ASC";

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String favoriteDishOrDrink = rs.getString("favorite_dish_or_drink");

                System.out.println("Client ID: " + clientId);
                System.out.println("Client Name: " + clientName);
                System.out.println("Favorite Dish or Drink: " + favoriteDishOrDrink);
                System.out.println("---------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateFavorite(int clientId, String oldDishName, String newDishName, Properties prop) {
        String sql = """
            UPDATE favorites 
            SET dish_id = (SELECT d.dish_id FROM dishes d WHERE d.name = ?)
            WHERE client_id = ? AND dish_id = (SELECT d.dish_id FROM dishes d WHERE d.name = ?)
            """;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            DishService dishService = new DishService();
            Dish oldDish = dishService.getDishByName(oldDishName, prop);
            Dish newDish = dishService.getDishByName(newDishName, prop);

            pstmt.setString(1, newDish.getName());
            pstmt.setInt(2, clientId);
            pstmt.setString(3, oldDish.getName());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Улюблена страва успішно оновлена для клієнта з ID " + clientId + "!");
            } else {
                System.out.println("Не вдалося оновити улюблену страву. Перевірте, чи страви існують.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
