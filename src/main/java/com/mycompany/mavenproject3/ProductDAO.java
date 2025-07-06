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

public class ProductDAO {
    private Connection conn;

    public ProductDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
                "root", ""
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Kesalahan koneksi ke database.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.");
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT id, code, name, category, price, stock, createdBy, editedBy FROM product";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

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
                p.setAuditInfo(audit);

                products.add(p);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data produk.");
        }
        return products;
    }

    public Product insertProduct(Product product) {
        try {
            String sql = "INSERT INTO product (code, name, category, price, stock, createdBy) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStock());
            stmt.setString(6, product.getAuditInfo() != null ? product.getAuditInfo().getCreatedBy() : null);

            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan produk baru.");
        }
        return product;
    }

    public Product updateProduct(Product product) {
        try {
            String sql = "UPDATE product SET code = ?, name = ?, category = ?, price = ?, stock = ?, editedBy = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStock());
            stmt.setString(6, product.getAuditInfo() != null ? product.getAuditInfo().getEditedBy() : null);
            stmt.setInt(7, product.getId());

            int affected = stmt.executeUpdate();
            stmt.close();

            return affected > 0 ? product : null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memperbarui data produk.");
            return null;
        }
    }

    public boolean deleteProduct(int productId) {
        try {
            String sql = "DELETE FROM product WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            int affected = stmt.executeUpdate();
            stmt.close();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus produk.");
            return false;
        }
    }

    public Product findById(int id) {
        Product product = null;
        try {
            String sql = "SELECT id, code, name, category, price, stock, createdBy, editedBy FROM product WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(
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
                product.setAuditInfo(audit);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengambil data produk berdasarkan ID.");
        }
        return product;
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}