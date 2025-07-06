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
import javax.swing.JOptionPane;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
            "root", ""
        );
    }

    // Generate next ID seperti C001, C002, ...
    private String generateNextId() throws SQLException {
        String lastId = null;
        String sql = "SELECT id FROM customer ORDER BY id DESC LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                lastId = rs.getString("id");
            }
        }

        if (lastId != null && lastId.startsWith("C")) {
            int num = Integer.parseInt(lastId.substring(1));
            return String.format("C%03d", num + 1);
        } else {
            return "C001";
        }
    }

    public Customer insertCustomer(Customer c) throws SQLException {
        if (c.getId() == null || c.getId().isEmpty()) {
            c.setId(generateNextId());
        }

        String sql = "INSERT INTO customer (id, name, phoneNumber, address, createdBy) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getId());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getPhoneNumber());
            stmt.setString(4, c.getAddress());
            stmt.setString(5, c.getAuditInfo() != null ? c.getAuditInfo().getCreatedBy() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data customer baru.");
            throw e;  // lempar supaya caller bisa tahu gagal
        }
        return c;
    }

    public Customer updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customer SET name=?, phoneNumber=?, address=?, editedBy=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhoneNumber());
            stmt.setString(3, c.getAddress());
            stmt.setString(4, c.getAuditInfo() != null ? c.getAuditInfo().getEditedBy() : null);
            stmt.setString(5, c.getId());

            int affected = stmt.executeUpdate();
            return affected > 0 ? c : null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memperbarui data customer.");
            throw e;
        }
    }

    public boolean deleteCustomer(String id) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus data customer.");
            throw e;
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, phoneNumber, address, createdBy, editedBy, deletedBy FROM customer";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer c = new Customer(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("phoneNumber"),
                    rs.getString("address")
                );
                AuditInfo audit = new AuditInfo();
                audit.setCreatedBy(rs.getString("createdBy"));
                audit.setEditedBy(rs.getString("editedBy"));
                audit.setDeletedBy(rs.getString("deletedBy"));
                c.setAuditInfo(audit);
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data customer.");
            throw e;
        }
        return customers;
    }

    public Customer findById(String id) throws SQLException {
        Customer customer = null;
        String sql = "SELECT id, name, phoneNumber, address, createdBy, editedBy, deletedBy FROM customer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("address")
                    );
                    AuditInfo audit = new AuditInfo();
                    audit.setCreatedBy(rs.getString("createdBy"));
                    audit.setEditedBy(rs.getString("editedBy"));
                    audit.setDeletedBy(rs.getString("deletedBy"));
                    customer.setAuditInfo(audit);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengambil data customer berdasarkan ID.");
            throw e;
        }
        return customer;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
