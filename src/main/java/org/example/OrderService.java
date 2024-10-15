package org.example;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OrderService {
    public void addOrder(Client client, List<Dish> dishes, Employee employee, LocalDate orderDate, Properties prop) {
        String sqlOrder = "INSERT INTO orders (client_id, employee_id, order_date, total_price) VALUES (?,?,?,?)";
        String sqlOrderItems = "INSERT INTO order_items (order_id, dish_id, quantity, price) VALUES (?,?,?,?)";

        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, client.getId());
            pstmt.setInt(2,employee.getId());
            pstmt.setDate(3, Date.valueOf(orderDate));
            pstmt.setBigDecimal(4, calculateTotalPrice(dishes));
            pstmt.executeUpdate();

            ResultSet generatedkeys = pstmt.getGeneratedKeys();

            if(generatedkeys.next()) {
                int orderid = generatedkeys.getInt(1);

                try(PreparedStatement pstmtOrderItems = conn.prepareStatement(sqlOrderItems)){
                    for(Dish dish : dishes) {
                        pstmtOrderItems.setInt(1, orderid);
                        pstmtOrderItems.setInt(2, dish.getId());
                        pstmtOrderItems.setInt(3, 1);
                        pstmtOrderItems.setBigDecimal(4, dish.getPrice());
                        pstmtOrderItems.executeUpdate();

                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private BigDecimal calculateTotalPrice(List<Dish> dishes) {
        return dishes.stream()
                .map(Dish::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public void deleteOrder(int id, Properties prop) {
        String sqlDeleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String sqlDeleteOrder = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(prop)) {
            // Видалити всі записи у таблиці order_items для цього замовлення
            try (PreparedStatement pstmtDeleteItems = conn.prepareStatement(sqlDeleteItems)) {
                pstmtDeleteItems.setInt(1, id);
                pstmtDeleteItems.executeUpdate();
            }

            // Після цього видалити запис у таблиці orders
            try (PreparedStatement pstmtDeleteOrder = conn.prepareStatement(sqlDeleteOrder)) {
                pstmtDeleteOrder.setInt(1, id);
                pstmtDeleteOrder.executeUpdate();
                System.out.println("Order deleted successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getOrderHistory(Properties prop) {
        String sql = """
                SELECT o.order_id, o.order_date, o.total_price, 
                       c.client_id, c.full_name AS client_name, 
                       e.employee_id, e.full_name AS employee_name
                FROM orders o
                JOIN clients c ON o.client_id = c.client_id
                JOIN employees e ON o.employee_id = e.employee_id
                """;

        List<Order> orderHistory = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {
            while(rs.next()) {
                // Отримання інформації про замовлення
                int orderId = rs.getInt("order_id");
                LocalDate orderDate = rs.getDate("order_date").toLocalDate();
                BigDecimal totalPrice = rs.getBigDecimal("total_price");

                // Створення об'єкта Client
                Client client = new Client(rs.getInt("client_id"), rs.getString("client_name"));

                // Створення об'єкта Employee
                Employee employee = new Employee(rs.getInt("employee_id"), rs.getString("employee_name"));

                // Отримання страв для цього замовлення
                List<Dish> dishes = getDishesForOrder(orderId, conn);

                // Створення замовлення
                Order order = new Order(orderId, client, employee, dishes, orderDate, totalPrice);
                orderHistory.add(order);
            }
            for (Order order : orderHistory) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Client: " + order.getClient().getFullname());
                System.out.println("Employee: " + order.getEmployee().getFullName());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Total Price: " + order.getTotalPrice());
                System.out.println("Dishes:");
                for (Dish dish : order.getDishes()) {
                    System.out.println(" - " + dish.getName() + " (Price: " + dish.getPrice() + ")");
                }
                System.out.println("---------------------------");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Dish> getDishesForOrder(int orderId, Connection conn) throws SQLException {
        String sql = """
                SELECT d.dish_id, d.name, d.price 
                FROM order_items oi
                JOIN dishes d ON oi.dish_id = d.dish_id
                WHERE oi.order_id = ?
                """;

        List<Dish> dishes = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = new Dish(rs.getInt("dish_id"), rs.getString("name"), rs.getBigDecimal("price"));
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }
    public void getOrderByClientName(String name, Properties prop) {
        String sql = """
            SELECT o.order_id, o.order_date, o.total_price, 
                   c.client_id, c.full_name AS client_name, 
                   e.employee_id, e.full_name AS employee_name
            FROM orders o
            JOIN clients c ON o.client_id = c.client_id
            JOIN employees e ON o.employee_id = e.employee_id
            WHERE c.full_name = ?
            """;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                do {
                    // Отримання метаданих ResultSet для динамічного виведення колонок
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Виведення всіх колонок замовлення
                    System.out.println("Інформація про замовлення:");
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        System.out.println(columnName + ": " + value);
                    }

                    // Отримання страв для цього замовлення
                    int orderId = rs.getInt("order_id");
                    List<Dish> dishes = getDishesForOrder(orderId, conn);

                    // Виведення страв
                    System.out.println("Dishes:");
                    for (Dish dish : dishes) {
                        System.out.println(" - " + dish.getName() + " (Price: " + dish.getPrice() + ")");
                    }
                    System.out.println("---------------------------");

                } while (rs.next());
            } else {
                System.out.println("Замовлення для клієнта з ім'ям " + name + " не знайдено.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void getOrderHistorySort(Properties prop) {
        String sql = """
            SELECT o.order_id, o.order_date, o.total_price, 
                   c.client_id, c.full_name AS client_name, 
                   e.employee_id, e.full_name AS employee_name
            FROM orders o
            JOIN clients c ON o.client_id = c.client_id
            JOIN employees e ON o.employee_id = e.employee_id
            ORDER BY o.total_price ASC -- Сортування за ціною замовлення
            """;

        List<Order> orderHistory = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Отримання інформації про замовлення
                int orderId = rs.getInt("order_id");
                LocalDate orderDate = rs.getDate("order_date").toLocalDate();
                BigDecimal totalPrice = rs.getBigDecimal("total_price");

                // Створення об'єкта Client
                Client client = new Client(rs.getInt("client_id"), rs.getString("client_name"));

                // Створення об'єкта Employee
                Employee employee = new Employee(rs.getInt("employee_id"), rs.getString("employee_name"));

                // Отримання страв для цього замовлення
                List<Dish> dishes = getDishesForOrder(orderId, conn);

                // Створення замовлення
                Order order = new Order(orderId, client, employee, dishes, orderDate, totalPrice);
                orderHistory.add(order);
            }

            // Виведення інформації про замовлення
            for (Order order : orderHistory) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Client: " + order.getClient().getFullname());
                System.out.println("Employee: " + order.getEmployee().getFullName());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Total Price: " + order.getTotalPrice());
                System.out.println("Dishes:");
                for (Dish dish : order.getDishes()) {
                    System.out.println(" - " + dish.getName() + " (Price: " + dish.getPrice() + ")");
                }
                System.out.println("---------------------------");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateOrder(int orderId, Client newClient, Employee newEmployee, LocalDate newOrderDate, List<Dish> newDishes, Properties prop) {
        String sqlUpdateOrder = "UPDATE orders SET client_id = ?, employee_id = ?, order_date = ?, total_price = ? WHERE order_id = ?";
        String sqlDeleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String sqlOrderItems = "INSERT INTO order_items (order_id, dish_id, quantity, price) VALUES (?,?,?,?)";

        try (Connection conn = DatabaseConnector.getConnection(prop)) {
            // Оновлення інформації про замовлення
            try (PreparedStatement pstmtUpdateOrder = conn.prepareStatement(sqlUpdateOrder)) {
                pstmtUpdateOrder.setInt(1, newClient.getId());
                pstmtUpdateOrder.setInt(2, newEmployee.getId());
                pstmtUpdateOrder.setDate(3, Date.valueOf(newOrderDate));
                pstmtUpdateOrder.setBigDecimal(4, calculateTotalPrice(newDishes));
                pstmtUpdateOrder.setInt(5, orderId);
                pstmtUpdateOrder.executeUpdate();
            }

            // Видалення старих страв
            try (PreparedStatement pstmtDeleteItems = conn.prepareStatement(sqlDeleteItems)) {
                pstmtDeleteItems.setInt(1, orderId);
                pstmtDeleteItems.executeUpdate();
            }

            // Додавання нових страв
            try (PreparedStatement pstmtOrderItems = conn.prepareStatement(sqlOrderItems)) {
                for (Dish dish : newDishes) {
                    pstmtOrderItems.setInt(1, orderId);
                    pstmtOrderItems.setInt(2, dish.getId());
                    pstmtOrderItems.setInt(3, 1); // Кількість - 1, якщо потрібно, змініть логіку
                    pstmtOrderItems.setBigDecimal(4, dish.getPrice());
                    pstmtOrderItems.executeUpdate();
                }
            }

            System.out.println("Замовлення оновлено успішно.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
