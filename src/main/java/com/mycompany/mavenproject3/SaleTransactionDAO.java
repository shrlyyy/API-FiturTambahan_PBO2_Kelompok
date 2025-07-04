/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SaleTransactionDAO {
    private Connection conn;

    public SaleTransactionDAO(Connection conn) {
        this.conn = conn;
    }

    public SaleTransactionDAO() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
            "root", ""
        );
}

    public int insertSaleTransaction(SaleTransaction sale) throws SQLException {
        String sql = "INSERT INTO saletransaction (customerId, cashierName, date, time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sale.getCustomerId());
            stmt.setString(2, sale.getCashierName());
            stmt.setDate(3, Date.valueOf(sale.getDate()));
            stmt.setTime(4, Time.valueOf(sale.getTime()));
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Membuat transaksi gagal, tidak ada baris yang diupdate.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Membuat transaksi gagal, tidak dapat memperoleh ID.");
                }
            }
        }
    }

}

