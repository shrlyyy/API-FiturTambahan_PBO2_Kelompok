/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaleItemDAO {
    private Connection conn;

    public SaleItemDAO(Connection conn) {
        this.conn = conn;
    }

    public SaleItemDAO() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
            "root", ""
        );
}

    public void insertSaleItem(SaleItem item, int saleId) throws SQLException {
        String sql = "INSERT INTO saleitem (saleId, productId, productName, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, item.getProductId());
            stmt.setString(3, item.getProductName());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getQuantity());
            stmt.setDouble(6, item.getSubTotal());
            stmt.executeUpdate();
        }
    }
}

