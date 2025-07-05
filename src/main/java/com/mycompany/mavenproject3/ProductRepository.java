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

public class ProductRepository {
    private Connection conn;

    public ProductRepository(Connection conn) {
        this.conn = conn;
    }

    public Product addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO product (code, name, category, price, stock, createdBy) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getAuditInfo().getCreatedBy());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
        return p;
    }

    public Product updateProduct(Product p) throws SQLException {
        String sql = "UPDATE product SET code=?, name=?, category=?, price=?, stock=?, editedBy=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getAuditInfo().getEditedBy());
            ps.setInt(7, p.getId());

            int affected = ps.executeUpdate();
            return affected > 0 ? p : null;
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE deletedBy IS NULL"; // Optional: hide soft-deleted
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                AuditInfo audit = new AuditInfo();
                audit.setCreatedBy(rs.getString("createdBy"));
                audit.setEditedBy(rs.getString("editedBy"));
                audit.setDeletedBy(rs.getString("deletedBy"));
                p.setAuditInfo(audit);
                products.add(p);
            }
        }
        return products;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ? AND deletedBy IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                    );
                    AuditInfo audit = new AuditInfo();
                    audit.setCreatedBy(rs.getString("createdBy"));
                    audit.setEditedBy(rs.getString("editedBy"));
                    audit.setDeletedBy(rs.getString("deletedBy"));
                    p.setAuditInfo(audit);
                    return p;
                }
            }
        }
        return null;
    }

}
