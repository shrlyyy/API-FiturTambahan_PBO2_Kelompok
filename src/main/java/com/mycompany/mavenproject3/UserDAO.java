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

import javax.swing.JOptionPane;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
                "root",
                ""
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Kesalahan koneksi ke database.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.");
        }
    }

    public Cashier login(String username, String password) throws SQLException {
        String query = "SELECT * FROM cashier WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        Cashier cashier = null;
        if (rs.next()) {
            int id = rs.getInt("id");
            cashier = new Cashier(id, username);
        }

        rs.close();
        stmt.close();
        return cashier;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
