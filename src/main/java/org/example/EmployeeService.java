package org.example;

import org.w3c.dom.CDATASection;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeService {
    public  void addEmployee(String fullName, Date birthDate, Properties prop) {
        String sql = "INSERT INTO employees (full_name, date_of_birth) VALUES(?,?)";
        try(Connection connection = DatabaseConnector.getConnection(prop);
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setDate(2, (java.sql.Date) birthDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int id, Properties prop){
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Employee deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployee(Employee employee, Properties prop){
        String sql = "UPDATE employees SET full_name = ?, date_of_birth = ? WHERE employee_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, employee.getFullName());
            pstmt.setDate(2, Date.valueOf(employee.getDateOfBirth()));
            pstmt.setLong(3, employee.getId());

            int RowsAffected = pstmt.executeUpdate();
            if(RowsAffected > 0){
                System.out.println("Employee updated successfully");
            } else {
                System.out.println("Employee not updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getEmployee(int id, Properties prop) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    System.out.println(columnName + " : " + rs.getObject(i)); // Виправлено на rs.getObject(i)
                }
            } else {
                System.out.println("Співробітника не знайдено.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Employee getEmployeeByIdNoOutput(int id, Properties prop) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        Employee employee = null;

        try (Connection conn = DatabaseConnector.getConnection(prop);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth");
                LocalDate dateOfBirth = sqlDateOfBirth.toLocalDate();
                employee = new Employee(id, name, dateOfBirth);
            } else {
                System.out.println("Працівника не знайдено!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }
    public void getAllEmployee(Properties prop) {
        String sql = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection(prop);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String fullname = rs.getString("full_name");
                LocalDate birthDay = rs.getDate("date_of_birth").toLocalDate();
                employees.add(new Employee(id, fullname, birthDay));
            }
            for(Employee employee : employees) {
                System.out.println("ID: " + employee.getId());
                System.out.println("Name: " + employee.getFullName());
                System.out.println("Birth Day: " + employee.getDateOfBirth());
                System.out.println("--------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
