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
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

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
    private JTextField dateField;
    private JTextField timeField;
    private JTextField idField;

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
    private String currentUser;

    public SellingForm(ProductForm productForm, List<Customer> customers, List<Reservation> reservations, String currentUser) {
        this.productForm = productForm;
        this.products = productForm.getProducts();
        this.customers = customers;
        this.reservations = reservations;
        this.currentUser = currentUser;

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

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        dateField.setText(currentDate.format(dateFormat));
        timeField.setText(currentTime.format(timeFormat));
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Info"));

        panel.add(new JLabel("Nomor Telepon:"));
        customerComboBox = new JComboBox<>();
        customerComboBox.setEditable(true);
        for (Customer c : customers) {
            customerComboBox.addItem(c.getPhoneNumber());
        }
        panel.add(customerComboBox);

        JTextField editor = (JTextField) customerComboBox.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCustomerFields();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCustomerFields();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCustomerFields();
            }
        });

        customerComboBox.addActionListener(e -> updateCustomerFields());

        panel.add(new JLabel("Nama Customer:"));
        nameField = new JTextField();
        nameField.setEditable(false);
        panel.add(nameField);

        panel.add(new JLabel("ID Customer:"));
        idField = new JTextField();
        idField.setEditable(false);
        panel.add(idField);

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

        summaryPanel.add(new JLabel("Tanggal:"));
        dateField = new JTextField();
        dateField.setEditable(false);
        summaryPanel.add(dateField);

        summaryPanel.add(new JLabel("Waktu:"));
        timeField = new JTextField();
        timeField.setEditable(false);
        summaryPanel.add(timeField);

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
        String input = ((JTextField) customerComboBox.getEditor().getEditorComponent()).getText().trim();

        if (input.isEmpty()) {
            nameField.setText("");
            idField.setText("");
            reservationField.setText("");
            return;
        }

        Customer matchedCustomer = null;
        for (Customer c : customers) {
            if (c.getPhoneNumber().startsWith(input)) {
                matchedCustomer = c;
                break;
            }
        }

        if (matchedCustomer != null) {
            nameField.setText(matchedCustomer.getName());
            idField.setText(matchedCustomer.getId());

            String resInfo = "-";
            for (Reservation r : reservations) {

                if (r.getCustomerId() != null && matchedCustomer.getId() != null &&
                r.getCustomerId().trim().equalsIgnoreCase(matchedCustomer.getId().trim())) {
                    resInfo = "Ada reservasi pada " + r.getReservationDate().toString();
                    break;
                }
            }
            reservationField.setText(resInfo);
        } else {
            nameField.setText("");
            idField.setText("");
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
                Currency.formatRupiah(product.getPrice()),
                qty,
                Currency.formatRupiah(subtotal)
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
                    productForm.loadProductData();
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
        for (SaleItem item : cartItems) {
            total += item.getSubTotal();
        }
        totalPriceField.setText(Currency.formatRupiah(total));
    }

    private void checkout(ActionEvent e) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/fiturtambahan-pbo2";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            conn.setAutoCommit(false); // transaction manual

            SaleTransactionDAO saleDAO = new SaleTransactionDAO(conn);
            SaleItemDAO itemDAO = new SaleItemDAO(conn);

        String selectedPhone = (String) customerComboBox.getSelectedItem();
        String customerId = null;
        for (Customer c : customers) {
            if (c.getPhoneNumber().equals(selectedPhone)) {
                customerId = c.getId();
                break;
            }
        }

    if (customerId == null) {
        JOptionPane.showMessageDialog(this, "Customer tidak ditemukan.");
        return;
    }


            LocalDate orderDate = LocalDate.now();
            LocalTime orderTime = LocalTime.now();

            SaleTransaction sale = new SaleTransaction(0, customerId, currentUser, orderDate, orderTime, cartItems);

            int saleId = saleDAO.insertSaleTransaction(sale);

            for (SaleItem item : cartItems) {
                itemDAO.insertSaleItem(item, saleId);
                Product p = item.getProduct();
                p.setStock(p.getStock() - item.getQuantity());
            }

            conn.commit();

            productForm.loadProductData();
            updateProductFields();

            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            dateField.setText(orderDate.format(dateFormat));
            timeField.setText(orderTime.format(timeFormat));

            String customerName = nameField.getText();
            String message = "Checkout berhasil untuk " + customerName + " (" + selectedPhone + ")!\n"
                + "Tanggal: " + orderDate.format(dateFormat) + "\n"
                + "Waktu: " + orderTime.format(timeFormat) + "\n"
                + "Total: Rp " + sale.getTotalPrice();
            JOptionPane.showMessageDialog(this, message);

            cartItems.clear();
            cartTableModel.setRowCount(0);
            updateTotal();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan transaksi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

