package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ClientService {
    public void addClient(String fullName, Date birthDate, Properties prop){
        String sql = "INSERT INTO clients (full_name, date_of_birth) VALUES(?,?)";
        try(Connection connection = DatabaseConnector.getConnection(prop);
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setDate(2, (java.sql.Date) birthDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Client getClientById(int id, Properties prop) {
        String sql = "SELECT * FROM clients WHERE client_id = ?";
        Client client = null;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    System.out.println(columnName + " : " + rs.getObject(i));
                }
                String name = rs.getString("full_name"); // Змінено на full_name
                java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth"); // Змінено на date_of_birth

                // Перетворення java.sql.Date на java.time.LocalDate
                LocalDate dateOfBirth = sqlDateOfBirth.toLocalDate();

                // Створення об'єкта Client
                client = new Client(id, name, dateOfBirth);
            } else {
                System.out.println("Клієнта не знайдено.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }

    public Client getClientByIdNoOutput(int id, Properties prop) {
        String sql = "SELECT * FROM clients WHERE client_id = ?";
        Client client = null;

        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                String name = rs.getString("full_name");
                java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth");
                LocalDate dateOfBirth = sqlDateOfBirth.toLocalDate();
                client = new Client(id, name, dateOfBirth);
            } else {
                System.out.println("Клієнта не знайдено!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    public Client getClientByNameNoOutput(String fullName, Properties prop) {
        String sql = "SELECT * FROM clients WHERE full_name = ?";
        Client client = null;

        try(Connection conn = DatabaseConnector.getConnection(prop);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                int id = rs.getInt("client_id");
                java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth");
                LocalDate dateOfBirth = sqlDateOfBirth.toLocalDate();
                client = new Client(id, fullName, dateOfBirth);
            } else {
                System.out.println("Клієнта не знайдено!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    public void deleteClient(int id, Properties prop){
        String sql = "DELETE FROM clients WHERE client_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0){
                System.out.println("Клієнта успішно видалено.");
            } else {
                System.out.println("Клієнта не знайдено.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateClient(Client client, Properties prop){
        String sql = "UPDATE clients SET full_name = ?, date_of_birth = ? WHERE client_id = ?";
        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.getFullname());
            pstmt.setDate(2, java.sql.Date.valueOf(client.getDateOfBirth()));
            pstmt.setInt(3, client.getId());

            int RowsAffected = pstmt.executeUpdate();
            if(RowsAffected > 0){
                System.out.println("Client successfully updated.");
            } else {
                System.out.println("Client failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getAllClients(Properties prop) {
        String sql = "SELECT * FROM clients";
        List<Client> clients = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection(prop);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                int id = rs.getInt("client_id");
                String fullname = rs.getString("full_name");
                LocalDate birthDay = rs.getDate("date_of_birth").toLocalDate();
                clients.add(new Client(id, fullname, birthDay));
            }
            for(Client client : clients) {
                System.out.println("ID: " + client.getId());
                System.out.println("Name: " + client.getFullname());
                System.out.println("Birth Day: " + client.getDateOfBirth());
                System.out.println("--------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void printClientsSortedByVisits(Properties prop) {
        String sql = "SELECT c.client_id, c.full_name, COUNT(o.order_id) AS visit_count " +
                "FROM clients c " +
                "JOIN orders o ON c.client_id = o.client_id " +
                "GROUP BY c.client_id, c.full_name " +
                "ORDER BY visit_count DESC";

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("full_name");
                int visitCount = rs.getInt("visit_count");

                System.out.println("Client ID: " + clientId);
                System.out.println("Client Name: " + clientName);
                System.out.println("Visit Count: " + visitCount);
                System.out.println("---------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void printClientsSortedByEmployeeFrequency(Properties prop) {
        String sql = "SELECT c.client_id, c.full_name AS client_name, e.full_name AS employee_name, COUNT(o.order_id) AS service_count " +
                "FROM clients c " +
                "JOIN orders o ON c.client_id = o.client_id " +
                "JOIN employees e ON o.employee_id = e.employee_id " +
                "GROUP BY c.client_id, c.full_name, e.full_name " +
                "ORDER BY service_count DESC";

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String employeeName = rs.getString("employee_name");
                int serviceCount = rs.getInt("service_count");

                System.out.println("Client ID: " + clientId);
                System.out.println("Client Name: " + clientName);
                System.out.println("Employee Name: " + employeeName);
                System.out.println("Service Count: " + serviceCount);
                System.out.println("---------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}