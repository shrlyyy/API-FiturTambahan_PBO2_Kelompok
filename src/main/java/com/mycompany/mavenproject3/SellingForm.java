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
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class SellingForm extends JFrame {
    private JComboBox<String> customerComboBox;
    private JTextField nameField;
    private JTextField reservationField;

    private JComboBox<String> productComboBox;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField qtyField;
    private JButton addToCartButton;
    private JButton deleteButton;
    private JButton checkoutButton;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JTextField totalPriceField;

    private List<Product> products;
    private List<Customer> customers;
    private List<Reservation> reservations;
    private List<SaleItem> cartItems = new ArrayList<>();
    private ProductForm productForm;

    private boolean isEditingCart = false;
    private int editingCartIndex = -1;

    public SellingForm(ProductForm productForm, List<Customer> customers, List<Reservation> reservations) {
        this.productForm = productForm;
        this.products = productForm.getProducts();
        this.customers = customers;
        this.reservations = reservations;

        setTitle("WK. Cuan | Form Penjualan");
        setSize(650, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(1, 2));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customer Info", createCustomerPanel());
        tabbedPane.addTab("Transaksi Penjualan", createSellingPanel());

        add(tabbedPane, BorderLayout.CENTER);

        updateCustomerFields();
        updateProductFields();
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Info"));

        panel.add(new JLabel("Nomor Telepon:"));
        customerComboBox = new JComboBox<>();
        customerComboBox.setEditable(true);
        for (Customer c : customers) {
            customerComboBox.addItem(c.getPhoneNumber().toString());
        }
        customerComboBox.addActionListener(e -> updateCustomerFields());
        panel.add(customerComboBox);

        panel.add(new JLabel("Nama Customer:"));
        nameField = new JTextField();
        nameField.setEditable(false);
        panel.add(nameField);

        panel.add(new JLabel("Reservasi:"));
        reservationField = new JTextField();
        reservationField.setEditable(false);
        panel.add(reservationField);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateCustomerFields());
        panel.add(new JLabel());
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createSellingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel atas: Form Produk
        JPanel productPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        productPanel.setBorder(BorderFactory.createTitledBorder("Pilih Produk"));

        productPanel.add(new JLabel("Produk:"));
        productComboBox = new JComboBox<>();
        for (Product p : products) productComboBox.addItem(p.getName());
        productComboBox.addActionListener(e -> updateProductFields());
        productPanel.add(productComboBox);

        productPanel.add(new JLabel("Harga:"));
        priceField = new JTextField();
        priceField.setEditable(false);
        productPanel.add(priceField);

        productPanel.add(new JLabel("Stok:"));
        stockField = new JTextField();
        stockField.setEditable(false);
        productPanel.add(stockField);

        productPanel.add(new JLabel("Qty:"));
        qtyField = new JTextField();
        productPanel.add(qtyField);

        panel.add(productPanel, BorderLayout.NORTH);

        // Tabel Keranjang
        cartTableModel = new DefaultTableModel(new String[]{"Produk", "Harga", "Qty", "Subtotal"}, 0);
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel bawah: tombol dan summary
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        addToCartButton = new JButton("Tambah ke Keranjang");
        deleteButton = new JButton("Hapus");
        buttonPanel.add(addToCartButton);
        buttonPanel.add(deleteButton);

        addToCartButton.addActionListener(this::addToCart);
        deleteButton.addActionListener(this::deleteSelectedItem);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Ringkasan"));

        summaryPanel.add(new JLabel("Total:"));
        totalPriceField = new JTextField();
        totalPriceField.setEditable(false);
        summaryPanel.add(totalPriceField);

        checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(this::checkout);
        summaryPanel.add(new JLabel());
        summaryPanel.add(checkoutButton);

        bottomPanel.add(summaryPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        cartTable.getSelectionModel().addListSelectionListener(e -> {
            int selected = cartTable.getSelectedRow();
            if (selected != -1) {
                String productName = cartTableModel.getValueAt(selected, 0).toString();
                int qty = Integer.parseInt(cartTableModel.getValueAt(selected, 2).toString());

                productComboBox.setSelectedItem(productName);
                qtyField.setText(String.valueOf(qty));

                isEditingCart = true;
                editingCartIndex = selected;

                addToCartButton.setText("Update Qty");
            }
        });

        return panel;
    }

    private void updateCustomerFields() {
        String selectedPhone = (String) customerComboBox.getSelectedItem();
        if (selectedPhone == null || selectedPhone.trim().isEmpty()) {
            nameField.setText("");
            reservationField.setText("");
            return;
        }

        Customer matchedCustomer = null;
        for (Customer c : customers) {
            if (c.getPhoneNumber().toString().equals(selectedPhone.trim())) {
                matchedCustomer = c;
                break;
            }
        }

        if (matchedCustomer != null) {
            nameField.setText(matchedCustomer.getName());

            // Cek apakah customer punya reservasi
            boolean hasReservation = false;
            int custIdNum = Integer.parseInt(matchedCustomer.getId().replaceAll("\\D+", ""));
            for (Reservation r : reservations) {
                if (r.getCustomerId() == custIdNum) {
                    hasReservation = true;
                    break;
                }
            }
            reservationField.setText(hasReservation ? "Ada Reservasi" : "Tidak Ada Reservasi");
        } else {
            nameField.setText("");
            reservationField.setText("");
        }
    }

    private void updateProductFields() {
        int selected = productComboBox.getSelectedIndex();
        if (selected != -1) {
            Product p = products.get(selected);
            priceField.setText(String.valueOf(p.getPrice()));
            stockField.setText(String.valueOf(p.getStock()));
        }
    }

    private void addToCart(ActionEvent e) {
    int selected = productComboBox.getSelectedIndex();
    if (selected == -1) return;

    Product product = products.get(selected);

    try {
        int qty = Integer.parseInt(qtyField.getText());
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Qty harus lebih dari 0.");
            return;
        }

        double subtotal = qty * product.getPrice();

        if (isEditingCart && editingCartIndex != -1) {
            // MODE EDIT
            SaleItem item = cartItems.get(editingCartIndex);
            item.setQuantity(qty);
            cartTableModel.setValueAt(qty, editingCartIndex, 2);
            cartTableModel.setValueAt(subtotal, editingCartIndex, 3);

            JOptionPane.showMessageDialog(this, "Item berhasil diperbarui.");
        } else {
            // MODE TAMBAH
            cartItems.add(new SaleItem(product, qty));
            cartTableModel.addRow(new Object[]{
                product.getName(),
                product.getPrice(),
                qty,
                subtotal
            });
        }

        updateTotal();
        updateProductFields();
        qtyField.setText("");
        cartTable.clearSelection();

        // Reset mode edit
        isEditingCart = false;
        editingCartIndex = -1;
        addToCartButton.setText("Tambah ke Keranjang");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Masukkan qty yang valid.");
    }
}

    private void deleteSelectedItem(ActionEvent e) {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            String productName = cartTableModel.getValueAt(selectedRow, 0).toString();
            int qty = Integer.parseInt(cartTableModel.getValueAt(selectedRow, 2).toString());

            cartItems.remove(selectedRow);
            cartTableModel.removeRow(selectedRow);

            // Kembalikan stok
            for (Product p : products) {
                if (p.getName().equals(productName)) {
                    p.setStock(p.getStock() + qty);
                    productForm.loadProductData(products);
                    updateProductFields();
                    break;
                }
            }

            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus.");
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += (double) cartTableModel.getValueAt(i, 3);
        }
        totalPriceField.setText("Rp " + total);
    }

    private void checkout(ActionEvent e) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong.");
            return;
        }

        for (SaleItem item : cartItems) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            p.setStock(p.getStock() - qty);
        }
        productForm.loadProductData(products);
        updateProductFields();

        String customerPhone = (String) customerComboBox.getSelectedItem();
        String customerName = nameField.getText();
        String message = "Checkout berhasil untuk " + customerName + " (" + customerPhone + ")!\nTotal: " + totalPriceField.getText();
        JOptionPane.showMessageDialog(this, message);

        cartItems.clear();
        cartTableModel.setRowCount(0);
        updateTotal();
    }
}