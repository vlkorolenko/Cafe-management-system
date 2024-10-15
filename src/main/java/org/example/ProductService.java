package org.example;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProductService {
    public void addProduct(String name, Date expiryDate, Properties prop) {
        String sql = "INSERT INTO products (name, expiry_date) VALUES (?, ?)";

        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDate(2, expiryDate);
            pstmt.executeUpdate();
            System.out.println("Product added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product, Properties prop) {
        String sql = "UPDATE products SET name = ?, expiry_date = ? WHERE product_id = ?";

        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, product.getName());
            pstmt.setDate(2, Date.valueOf(product.getExpiryDate()));
            pstmt.setInt(3, product.getId());

            int RowsAffected = pstmt.executeUpdate();

            if(RowsAffected > 0){
                System.out.println("Product updated successfully");
            } else {
                System.out.println("Product not updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id, Properties prop) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Product deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getAllProducts(Properties prop){
        String sql = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection(prop);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                LocalDate expiry = rs.getDate("expiry_date").toLocalDate();
                products.add(new Product(id, name, expiry));
            }
            for(Product product : products) {
                System.out.println("ID: " + product.getId());
                System.out.println("Name: " + product.getName());
                System.out.println("Expiry Date: " + product.getExpiryDate());
                System.out.println("--------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product getProduct(int id, Properties prop) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;

        try(Connection conn = DatabaseConnector.getConnection(prop);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if(rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    System.out.println(columnName + " : " + rs.getObject(i));

                    if("name".equals(columnName)) {
                        String name = rs.getString("name");
                    } else if ("expiry_date".equals(columnName)) {
                        java.sql.Date sqlExpiryDate = rs.getDate("expiry_date");
                        LocalDate expiryDate = sqlExpiryDate.toLocalDate();

                        product = new Product(rs.getInt("product_id"), "name", expiryDate);
                    }
                }
            }else {
                System.out.println("Product not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
