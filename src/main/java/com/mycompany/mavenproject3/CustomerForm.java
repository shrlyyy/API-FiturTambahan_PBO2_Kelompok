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
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerForm extends JFrame {
    private JTextField nameField;
    private JTextField phoneNumberField;
    private JTextField addressField;
    private JButton saveButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    private ArrayList<String> registeredPhones = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private boolean isEditing = false;
    private int editingIndex = -1;
    private String currentUser;
    private CustomerDAO customerDAO;

    public CustomerForm(String currentUser) {
        this.currentUser = currentUser;

        setTitle("WK. Cuan | Form Customer");
        setSize(700, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel form input
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nama:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Nomor Telepon:"));
        phoneNumberField = new JTextField();
        formPanel.add(phoneNumberField);

        formPanel.add(new JLabel("Alamat:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        // Panel tombol simpan dan hapus
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        saveButton = new JButton("Simpan");
        deleteButton = new JButton("Hapus");
        refreshButton = new JButton("Refresh");
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Table untuk menampilkan customer
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Nomor Telepon", "Alamat", "Last Action By"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing di tabel
            }
        };
        customerTable = new JTable(tableModel);
        getContentPane().add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Inisialisasi DAO dan load data
        try {
            customerDAO = new CustomerDAO();
            refreshCustomerDataFromDB();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi/muat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Event tombol simpan
        saveButton.addActionListener(e -> saveCustomer());

        // Event tombol hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                Customer removed = customers.get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus customer ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = customerDAO.deleteCustomer(removed.getId());
                        if (success) {
                            refreshCustomerDataFromDB();
                            refreshTable();
                            clearFields();
                            isEditing = false;
                            editingIndex = -1;
                        } else {
                            JOptionPane.showMessageDialog(this, "Gagal menghapus dari database.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error saat hapus data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih customer untuk dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Event pilih data dari tabel
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    Customer c = customers.get(selectedRow);
                    nameField.setText(c.getName());
                    phoneNumberField.setText(c.getPhoneNumber());
                    addressField.setText(c.getAddress());
                    editingIndex = selectedRow;
                    isEditing = true;
                } else {
                    clearFields();
                    isEditing = false;
                    editingIndex = -1;
                }
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                refreshCustomerDataFromDB();
                refreshTable();
                clearFields();
                isEditing = false;
                editingIndex = -1;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal refresh data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private void saveCustomer() {
        try {
            String name = nameField.getText().trim();
            String phoneText = phoneNumberField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phoneText.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phoneText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Nomor telepon harus berupa angka!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isEditing && editingIndex != -1) {
                Customer existing = customers.get(editingIndex);
                // Cek duplikat nomor telepon jika diganti
                if (!existing.getPhoneNumber().equals(phoneText) && isPhoneRegistered(phoneText)) {
                    JOptionPane.showMessageDialog(this, "Nomor telepon sudah terdaftar!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                existing.setName(name);
                existing.setPhoneNumber(phoneText);
                existing.setAddress(address);
                existing.getAuditInfo().setEditedBy(currentUser);

                customerDAO.updateCustomer(existing);
            } else {
                if (isPhoneRegistered(phoneText)) {
                    JOptionPane.showMessageDialog(this, "Nomor telepon sudah terdaftar!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Customer newCustomer = new Customer(name, phoneText, address);
                newCustomer.getAuditInfo().setCreatedBy(currentUser);
                customerDAO.insertCustomer(newCustomer);
            }

            refreshCustomerDataFromDB();
            refreshTable();
            clearFields();
            isEditing = false;
            editingIndex = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan/update ke database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPhoneRegistered(String phone) {
        for (Customer c : customers) {
            if (c.getPhoneNumber().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    private void refreshCustomerDataFromDB() throws SQLException {
        customers.clear();
        customers.addAll(customerDAO.getAllCustomers());
    }

    private void clearFields() {
        nameField.setText("");
        phoneNumberField.setText("");
        addressField.setText("");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            String lastActionBy = "-";
            AuditInfo audit = c.getAuditInfo();
            if (audit.getDeletedBy() != null && !audit.getDeletedBy().isEmpty()) {
                lastActionBy = "Deleted by " + audit.getDeletedBy();
            } else if (audit.getEditedBy() != null && !audit.getEditedBy().isEmpty()) {
                lastActionBy = "Edited by " + audit.getEditedBy();
            } else if (audit.getCreatedBy() != null && !audit.getCreatedBy().isEmpty()) {
                lastActionBy = "Created by " + audit.getCreatedBy();
            }
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getPhoneNumber(),
                c.getAddress(),
                lastActionBy
            });
        }
        customerTable.revalidate();
        customerTable.repaint();
    }
}
