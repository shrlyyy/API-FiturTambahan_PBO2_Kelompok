/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReservationForm extends JFrame {
    private JComboBox<String> phoneComboBox;
    private JTextField idField;
    private JTextField nameField;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private JTextField numberOfPeopleField;
    private JTextField tableField;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private boolean isEditing = false;
    private String editingReservationId = null;

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    private ArrayList<Customer> customers;
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private String currentUser;
    private ReservationDAO reservationDAO;

    public ReservationForm(ArrayList<Customer> customers, String currentUser) {
        this.customers = customers;
        this.currentUser = currentUser;

        setTitle("WK. Cuan | Form Reservasi");
        setSize(850, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel form dengan GridLayout
        JPanel formPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nomor Telepon:"));
        phoneComboBox = new JComboBox<>();
        phoneComboBox.setEditable(true);

        for (Customer c : customers) {
            phoneComboBox.addItem(c.getPhoneNumber());
        }
        formPanel.add(phoneComboBox);

        formPanel.add(new JLabel("ID Customer:"));
        idField = new JTextField();
        idField.setEditable(false);
        formPanel.add(idField);

        formPanel.add(new JLabel("Nama Customer:"));
        nameField = new JTextField();
        nameField.setEditable(false);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Tanggal Reservasi:"));
        datePicker = new DatePicker();

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesBeforeCommonEra("dd-MM-yyyy");;
        datePicker.setSettings(dateSettings);
        
        formPanel.add(datePicker);

        formPanel.add(new JLabel("Jam Reservasi:"));
        timePicker = new TimePicker();
        formPanel.add(timePicker);

        formPanel.add(new JLabel("Meja:"));
        tableField = new JTextField();
        formPanel.add(tableField);

        formPanel.add(new JLabel("Jumlah Orang:"));
        numberOfPeopleField = new JTextField();
        formPanel.add(numberOfPeopleField);

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

        // Tabel reservasi
        tableModel = new DefaultTableModel(new String[]{"ID Reservasi", "ID Customer", "Nama", "Tanggal", "Jam", "Meja", "Jumlah Orang", "Last Action By:"}, 0);
        reservationTable = new JTable(tableModel);
        getContentPane().add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        try {
            reservationDAO = new ReservationDAO(); // mirip seperti customerDAO di CustomerForm
            reservations = new ArrayList<>(reservationDAO.getAllReservations());
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load reservasi: " + e.getMessage());
        }

        JTextField editor = (JTextField) phoneComboBox.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCustomerInfo();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCustomerInfo();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCustomerInfo();
            }
        });

        saveButton.addActionListener(e -> saveReservation());
        deleteButton.addActionListener(e -> deleteReservation());

        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = reservationTable.getSelectedRow();
                if (selectedRow != -1) {
                    String resIdStr = tableModel.getValueAt(selectedRow, 0).toString(); // R001
                    editingReservationId = resIdStr;
                    isEditing = true;

                    String custIdStr = tableModel.getValueAt(selectedRow, 1).toString();
                    Object custNameObj = tableModel.getValueAt(selectedRow, 2);
                    Object dateObj = tableModel.getValueAt(selectedRow, 3);
                    Object timeObj = tableModel.getValueAt(selectedRow, 4);
                    Object tableObj = tableModel.getValueAt(selectedRow, 5);
                    Object numPeopleObj = tableModel.getValueAt(selectedRow, 6);

                    idField.setText(custIdStr);
                    nameField.setText(custNameObj.toString());
                    phoneComboBox.setSelectedItem(getPhoneByCustomerId(custIdStr));
                    tableField.setText(tableObj.toString());
                    numberOfPeopleField.setText(numPeopleObj.toString());

                    try {
                        LocalDate ld = LocalDate.parse(dateObj.toString());
                        datePicker.setDate(ld);
                    } catch (Exception ex) {
                        datePicker.clear();
                    }

                    try {
                        LocalTime lt = LocalTime.parse(timeObj.toString());
                        timePicker.setTime(lt);
                    } catch (Exception ex) {
                        timePicker.clear();
                    }
                }
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                reservations = new ArrayList<>(reservationDAO.getAllReservations()); // reload dari DB
                clearFields();
                isEditing = false;
                editingReservationId = null;
                refreshTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal refresh data: " + ex.getMessage());
            }
        });
    }

    private String getPhoneByCustomerId(String custId) {
        for (Customer c : customers) {
            if (c.getId().equals(custId)) {
                return c.getPhoneNumber();
            }
        }
        return "";
    }

    private void updateCustomerInfo() {
        String input = ((JTextField) phoneComboBox.getEditor().getEditorComponent()).getText().trim();
        if (input.isEmpty()) {
            idField.setText("");
            nameField.setText("");
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
            idField.setText(matchedCustomer.getId());
            nameField.setText(matchedCustomer.getName());
        } else {
            idField.setText("");
            nameField.setText("");
        }
    }

    private void saveReservation() {
        String idCust = idField.getText().trim();
        String nameCust = nameField.getText().trim();
        String table = tableField.getText().trim();
        String numPeopleStr = numberOfPeopleField.getText().trim();

        if (idCust.isEmpty() || nameCust.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cari dan pilih customer terlebih dahulu.");
            return;
        }

        if (table.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Meja tidak boleh kosong.");
            return;
        }

        int numPeople;
        try {
            numPeople = Integer.parseInt(numPeopleStr);
            if (numPeople <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah orang harus angka lebih dari 0.");
            return;
        }

        LocalDate localDate = datePicker.getDate();
        LocalTime localTime = timePicker.getTime();

        if (localDate == null || localTime == null) {
            JOptionPane.showMessageDialog(this, "Tanggal dan jam reservasi harus diisi.");
            return;
        }

        try {
            if (isEditing && editingReservationId != null) {
                for (Reservation r : reservations) {
                    if (r.getReservationId().equals(editingReservationId)) {
                        r.setCustomerId(idCust);
                        r.setReservationDate(localDate);
                        r.setReservationTime(localTime);
                        r.setTable(table);
                        r.setNumberOfPeople(numPeople);
                        r.setEditedBy(currentUser);
                        r.setCreatedBy(null);
                        r.setDeletedBy(null);

                        reservationDAO.updateReservation(r);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Reservasi berhasil diperbarui.");
            } else {
                Reservation newRes = new Reservation(
                    null, // biar DAO generate otomatis
                    idCust,
                    localDate,
                    localTime,
                    table,
                    numPeople,
                    currentUser,
                    null,
                    null
                );
                reservationDAO.insertReservation(newRes);
                reservations.add(newRes); // tambahkan hasil dari DB
                JOptionPane.showMessageDialog(this, "Reservasi berhasil disimpan.");
            }

            clearFields();
            reservations = new ArrayList<>(reservationDAO.getAllReservations());
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan reservasi: " + ex.getMessage());
        }
    }


    private void deleteReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow != -1) {
            String resIdStr = tableModel.getValueAt(selectedRow, 0).toString(); // "R001"
            int resId = Integer.parseInt(resIdStr.replaceAll("\\D+", ""));      // ambil angka 001 → int

            Reservation toRemove = null;
            for (Reservation r : reservations) {
                if (r.getReservationId().equals(resIdStr)) {
                    toRemove = r;
                    break;
                }
            }

            if (toRemove != null) {
                try {
                    String formattedId = "R" + String.format("%03d", resId);
                    reservationDAO.deleteReservation(formattedId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal hapus reservasi dari database: " + ex.getMessage());
                    return;
                }

                reservations.remove(toRemove);
                JOptionPane.showMessageDialog(this, "Reservasi berhasil dihapus.");
                refreshTable();
                clearFields();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih reservasi yang ingin dihapus.");
        }
    }


    private void clearFields() {
        ((JTextField) phoneComboBox.getEditor().getEditorComponent()).setText("");
        idField.setText("");
        nameField.setText("");
        tableField.setText("");
        numberOfPeopleField.setText("");
        datePicker.clear();
        timePicker.clear();
        isEditing = false;
        editingReservationId = null;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Reservation r : reservations) {
            String custName = findCustomerNameById(r.getCustomerId());

            tableModel.addRow(new Object[]{
                r.getReservationId(),
                r.getCustomerId(),
                custName,
                r.getReservationDate(),
                r.getReservationTime().format(timeFormatter),
                r.getTable(),
                r.getNumberOfPeople(),
            });
        }
    }

    private String findCustomerNameById(String customerId) {
        for (Customer c : customers) {
            if (c.getId().equals(customerId)) {
                return c.getName();
            }
        }
        return "(Unknown)";
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }
}
