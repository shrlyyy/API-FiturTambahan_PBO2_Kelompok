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
    private JButton deleteButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    private ArrayList<String> registeredPhones = new ArrayList<>();
    private ArrayList<Customer> customers;
    private boolean isEditing = false;
    private int editingIndex = -1;
    private String currentUser;
    private CustomerDAO customerDAO;

    public CustomerForm(ArrayList<Customer> customers, String currentUser) {
        this.customers = customers;
        this.currentUser = currentUser;

        setTitle("WK. Cuan | Form Customer");
        setSize(700, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form Panel
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

        // Tombol Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        saveButton = new JButton("Simpan");
        deleteButton = new JButton("Hapus");
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Nomor Telepon", "Alamat", "Last Action By:"}, 0);
        customerTable = new JTable(tableModel);
        getContentPane().add(new JScrollPane(customerTable), BorderLayout.CENTER);

        try {
            customerDAO = new CustomerDAO();
            customers.clear();
            customers.addAll(customerDAO.getAllCustomers());
            registeredPhones.clear();
            for (Customer c : customers) {
                registeredPhones.add(c.getPhoneNumber().toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi/muat data: " + e.getMessage());
        }

        refreshTable();

        // Event tombol
        saveButton.addActionListener(e -> saveCustomer());

        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                Customer removed = customers.get(selectedRow);
                try {
                    // Panggil dengan dua parameter id dan username yang menghapus
                    customerDAO.deleteCustomer(removed.getId(), currentUser);
                    
                    customers.remove(selectedRow);
                    registeredPhones.remove(removed.getPhoneNumber().toString());
                    refreshTable();
                    clearFields();
                    isEditing = false;
                    editingIndex = -1;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus dari database: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih customer untuk dihapus.");
            }
        });

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    Customer c = customers.get(selectedRow);
                    nameField.setText(c.getName());
                    phoneNumberField.setText(c.getPhoneNumber().toString());
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
    }

    private void saveCustomer() {
        try {
            String name = nameField.getText().trim();
            String phoneText = phoneNumberField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phoneText.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!");
                return;
            }

            if (!phoneText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Nomor telepon harus berupa angka!");
                return;
            }

            Long phoneNumber = Long.parseLong(phoneText);

            if (isEditing && editingIndex != -1) {
                Customer existing = customers.get(editingIndex);
                if (!existing.getPhoneNumber().toString().equals(phoneText) && registeredPhones.contains(phoneText)) {
                    JOptionPane.showMessageDialog(this, "Nomor telepon sudah terdaftar!");
                    return;
                }
                registeredPhones.remove(existing.getPhoneNumber().toString());
                registeredPhones.add(phoneText);

                existing.setName(name);
                existing.setPhoneNumber(phoneNumber);
                existing.setAddress(address);
                existing.getAuditInfo().setEditedBy(currentUser);

                try {
                    customerDAO.updateCustomer(existing);
                    // reload data dari DB agar update audit info dan lain2 sinkron
                    customers.clear();
                    customers.addAll(customerDAO.getAllCustomers());
                    registeredPhones.clear();
                    for (Customer c : customers) {
                        registeredPhones.add(c.getPhoneNumber().toString());
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Gagal update ke database: " + e.getMessage());
                }
            } else {
                if (registeredPhones.contains(phoneText)) {
                    JOptionPane.showMessageDialog(this, "Nomor telepon sudah terdaftar!");
                    return;
                }

                Customer newCustomer = new Customer(name, phoneNumber, address);
                newCustomer.getAuditInfo().setCreatedBy(currentUser);
                try {
                    customerDAO.insertCustomer(newCustomer);
                    customers.clear();
                    customers.addAll(customerDAO.getAllCustomers());
                    registeredPhones.clear();
                    for (Customer c : customers) {
                        registeredPhones.add(c.getPhoneNumber().toString());
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Gagal simpan ke database: " + e.getMessage());
                }
            }

            refreshTable();
            clearFields();
            isEditing = false;
            editingIndex = -1;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    }
}