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
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
            "root", ""
        );
    }

    public void insertCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customer (id, name, phoneNumber, address, createdBy) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getId());
            stmt.setString(2, c.getName());
            stmt.setLong(3, c.getPhoneNumber());
            stmt.setString(4, c.getAddress());
            stmt.setString(5, c.getAuditInfo().getCreatedBy());
            stmt.executeUpdate();
        }
    }

    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customer SET name=?, phoneNumber=?, address=?, editedBy=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName());
            stmt.setLong(2, c.getPhoneNumber());
            stmt.setString(3, c.getAddress());
            stmt.setString(4, c.getAuditInfo().getEditedBy());
            stmt.setString(5, c.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(String id, String deletedBy) throws SQLException {
        String sql = "UPDATE customer SET deletedBy=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deletedBy);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT id, name, phoneNumber, address, createdBy, editedBy, deletedBy FROM customer";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Customer c = new Customer(
                rs.getString("id"),
                rs.getString("name"),
                rs.getLong("phoneNumber"),
                rs.getString("address")
            );
            c.getAuditInfo().setCreatedBy(rs.getString("createdBy"));
            c.getAuditInfo().setEditedBy(rs.getString("editedBy"));
            c.getAuditInfo().setDeletedBy(rs.getString("deletedBy"));
            list.add(c);
        }

        rs.close();
        stmt.close();
        return list;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
