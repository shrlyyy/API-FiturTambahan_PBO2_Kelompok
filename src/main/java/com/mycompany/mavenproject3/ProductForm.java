/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.util.ArrayList;
import java.util.List;

public class ProductForm extends JFrame {
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField codeField;
    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JTextField priceField;
    private JTextField stockField;
    private JButton saveButton;
    private JButton deleteButton;
    private List<Product> products;
    private boolean isEditing = false;
    private int editingIndex = -1;
    private Cashier currentUser;
    private ProductDAO productDAO;

    public interface ProductChangeListener {
        void onProductsChanged();
    }
    private ProductChangeListener listener;

    public void setProductChangeListener(ProductChangeListener listener) {
        this.listener = listener;
    }

    public List<Product> getProducts() {
        return products;
    }

    public ProductForm(Cashier currentUser) {
        this.currentUser = currentUser;
        this.productDAO = new ProductDAO();

        products = new ArrayList<>();
        
        setTitle("WK. Cuan | Stok Barang");
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel form pemesanan
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Kode Barang"));
        codeField = new JTextField();
        formPanel.add(codeField);
        
        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>(new String[]{"Coffee", "Dairy", "Juice", "Soda", "Tea"});
        formPanel.add(categoryField);
        
        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);
        
        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        saveButton = new JButton("Simpan");
        deleteButton = new JButton("Hapus");

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Kode", "Nama", "Kategori", "Harga Jual", "Stok", "Last Action By:"}, 0);
        drinkTable = new JTable(tableModel);
        getContentPane().add(new JScrollPane(drinkTable), BorderLayout.CENTER);

        loadProductData();

        saveButton.addActionListener(e -> saveProduct());

        deleteButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
            if (selectedRow != -1) {
                Product selectedProduct = products.get(selectedRow);
                productDAO.deleteProduct(selectedProduct.getId());

                loadProductData();
                clearFields();
                isEditing = false;
                editingIndex = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Pilih produk untuk menghapus.");
            }
        });


        drinkTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = drinkTable.getSelectedRow();
                if (selectedRow != -1) {
                    Product product = products.get(selectedRow);
                    codeField.setText(product.getCode());
                    nameField.setText(product.getName());
                    categoryField.setSelectedItem(product.getCategory());
                    priceField.setText(String.valueOf(product.getPrice()));
                    stockField.setText(String.valueOf(product.getStock()));
                    
                    editingIndex = selectedRow;
                    isEditing = true;
                } else {
                    clearFields();
                    isEditing = false;
                }
            }
        });
    }
    
        private void saveProduct() {
            try {
                String code = codeField.getText();
                String name = nameField.getText();
                String category = (String) categoryField.getSelectedItem();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());

                if (isEditing && editingIndex != -1) {
                    Product existing = products.get(editingIndex);
                    existing.setCode(code);
                    existing.setName(name);
                    existing.setCategory(category);
                    existing.setPrice(price);
                    existing.setStock(stock);
                    existing.getAuditInfo().setEditedBy(currentUser.getUsername());

                    productDAO.updateProduct(existing);
                } else {
                    Product newProduct = new Product(0, code, name, category, price, stock);
                    AuditInfo audit = new AuditInfo();
                    audit.setCreatedBy(currentUser.getUsername());
                    newProduct.setAuditInfo(audit);

                    productDAO.insertProduct(newProduct);
                }

                loadProductData();
                clearFields();
                isEditing = false;
                editingIndex = -1;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void clearFields() {
            codeField.setText("");
            nameField.setText("");
            priceField.setText("");
            stockField.setText("");
            categoryField.setSelectedIndex(0);
        }

    public void loadProductData() {
        products = productDAO.getAllProducts();
            tableModel.setRowCount(0);
            for (Product product : products) {
                String lastAction = "-";
                AuditInfo audit = product.getAuditInfo();
                if (audit != null) {
                    lastAction = audit.getEditedBy() != null ? audit.getEditedBy() : audit.getCreatedBy();
                }
                tableModel.addRow(new Object[]{
                    product.getCode(),
                    product.getName(),
                    product.getCategory(),
                    Currency.formatRupiah(product.getPrice()),
                    product.getStock(),
                    lastAction
                });
            }

            if (listener != null) listener.onProductsChanged();
        }


    public String getProductBannerText() {
        if (products.isEmpty()) return "Menu tidak tersedia";
        StringBuilder sb = new StringBuilder("Menu yang tersedia: ");
        for (int i = 0; i < products.size(); i++) {
            sb.append(products.get(i).getName());
            if (i < products.size() - 1) sb.append(" | ");
        }
        return sb.toString();
    }
}
